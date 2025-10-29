document.addEventListener('DOMContentLoaded', function() {
    const currencySelect = document.getElementById('chartCurrency');
    const dateFrom = document.getElementById('dateFrom');
    const dateTo = document.getElementById('dateTo');
    const loadChartButton = document.getElementById('loadChart');
    const periodButtons = document.querySelectorAll('.period-btn');
    
    // Установка дат по умолчанию
    const today = new Date();
    const monthAgo = new Date(today.getTime() - 30 * 24 * 60 * 60 * 1000);
    
    if (dateTo) dateTo.value = today.toISOString().split('T')[0];
    if (dateFrom) dateFrom.value = monthAgo.toISOString().split('T')[0];
    
    // Tooltip элемент
    let tooltip = null;
    let currentData = null;
    let currentCurrency = null;
    
    // Обработка кнопок периодов
    periodButtons.forEach(button => {
        button.addEventListener('click', function() {
            const days = parseInt(this.getAttribute('data-period'));
            
            // Снимаем активность со всех кнопок
            periodButtons.forEach(btn => btn.classList.remove('active'));
            // Активируем текущую
            this.classList.add('active');
            
            // Устанавливаем даты
            const newDateTo = new Date();
            const newDateFrom = new Date(newDateTo.getTime() - days * 24 * 60 * 60 * 1000);
            
            dateTo.value = newDateTo.toISOString().split('T')[0];
            dateFrom.value = newDateFrom.toISOString().split('T')[0];
            
            // Загружаем график
            loadChart();
        });
    });
    
    if (loadChartButton) {
        loadChartButton.addEventListener('click', function() {
            // Снимаем активность со всех кнопок периодов при ручном выборе
            periodButtons.forEach(btn => btn.classList.remove('active'));
            loadChart();
        });
        
        // Загрузить график при загрузке страницы
        loadChart();
    }
    
    async function loadChart() {
        const currency = currencySelect.value;
        const from = dateFrom.value;
        const to = dateTo.value;
        
        if (!from || !to) {
            alert('Пожалуйста, выберите период');
            return;
        }
        
        try {
            loadChartButton.disabled = true;
            loadChartButton.textContent = 'Загрузка...';
            
            const response = await fetch(
                `/charts/data?currency=${currency}&from=${from}&to=${to}`
            );
            
            if (!response.ok) {
                throw new Error('Ошибка загрузки данных');
            }
            
            const data = await response.json();
            currentData = data;
            currentCurrency = currency;
            renderChart(data, currency);
        } catch (error) {
            console.error('Chart loading error:', error);
            alert('Ошибка при загрузке графика');
        } finally {
            loadChartButton.disabled = false;
            loadChartButton.textContent = 'Показать график';
        }
    }
    
    function renderChart(data, currency) {
        const canvas = document.getElementById('rateChart');
        if (!canvas) return;
        
        const ctx = canvas.getContext('2d');
        const container = canvas.parentElement;
        
        // Создаем tooltip если его нет
        if (!tooltip) {
            tooltip = document.createElement('div');
            tooltip.className = 'chart-tooltip';
            container.style.position = 'relative';
            container.appendChild(tooltip);
        }
        
        // Устанавливаем размеры canvas
        canvas.width = container.offsetWidth - 64;
        canvas.height = 450;
        
        // Очистка
        ctx.clearRect(0, 0, canvas.width, canvas.height);
        
        if (data.length === 0) {
            ctx.fillStyle = '#6c757d';
            ctx.font = '18px sans-serif';
            ctx.textAlign = 'center';
            ctx.fillText('Нет данных для отображения', canvas.width / 2, canvas.height / 2);
            return;
        }
        
        const padding = {
            top: 60,
            right: 60,
            bottom: 80,
            left: 90
        };
        
        const chartWidth = canvas.width - padding.left - padding.right;
        const chartHeight = canvas.height - padding.top - padding.bottom;
        
        const values = data.map(d => d.value);
        const minValue = Math.min(...values) * 0.98;
        const maxValue = Math.max(...values) * 1.02;
        const valueRange = maxValue - minValue;
        
        // Массив точек для интерактивности
        const points = [];
        
        // Фон
        ctx.fillStyle = '#fafbfc';
        ctx.fillRect(padding.left, padding.top, chartWidth, chartHeight);
        
        // Сетка
        ctx.strokeStyle = '#e1e8ed';
        ctx.lineWidth = 1;
        
        for (let i = 0; i <= 6; i++) {
            const y = padding.top + (chartHeight / 6) * i;
            ctx.beginPath();
            ctx.moveTo(padding.left, y);
            ctx.lineTo(canvas.width - padding.right, y);
            ctx.stroke();
            
            const value = maxValue - (valueRange / 6) * i;
            ctx.fillStyle = '#6c757d';
            ctx.font = 'bold 14px sans-serif';
            ctx.textAlign = 'right';
            ctx.fillText(value.toFixed(4) + ' Br', padding.left - 15, y + 5);
        }
        
        // Рамка
        ctx.strokeStyle = '#d1d5db';
        ctx.lineWidth = 2;
        ctx.strokeRect(padding.left, padding.top, chartWidth, chartHeight);
        
        // Градиент
        const gradient = ctx.createLinearGradient(0, padding.top, 0, canvas.height - padding.bottom);
        gradient.addColorStop(0, 'rgba(80, 115, 232, 0.4)');
        gradient.addColorStop(1, 'rgba(80, 115, 232, 0.05)');
        
        ctx.fillStyle = gradient;
        ctx.beginPath();
        
        data.forEach((point, index) => {
            const x = padding.left + (chartWidth / (data.length - 1)) * index;
            const y = canvas.height - padding.bottom - ((point.value - minValue) / valueRange) * chartHeight;
            
            points.push({ x, y, value: point.value, index });
            
            if (index === 0) {
                ctx.moveTo(x, canvas.height - padding.bottom);
                ctx.lineTo(x, y);
            } else {
                ctx.lineTo(x, y);
            }
        });
        
        ctx.lineTo(canvas.width - padding.right, canvas.height - padding.bottom);
        ctx.closePath();
        ctx.fill();
        
        // Линия графика
        ctx.shadowColor = 'rgba(80, 115, 232, 0.3)';
        ctx.shadowBlur = 8;
        ctx.shadowOffsetY = 2;
        ctx.strokeStyle = '#5073e8';
        ctx.lineWidth = 3;
        ctx.lineJoin = 'round';
        ctx.lineCap = 'round';
        ctx.beginPath();
        
        points.forEach((point, index) => {
            if (index === 0) {
                ctx.moveTo(point.x, point.y);
            } else {
                ctx.lineTo(point.x, point.y);
            }
        });
        
        ctx.stroke();
        ctx.shadowColor = 'transparent';
        
        // Точки
        points.forEach(point => {
            ctx.beginPath();
            ctx.fillStyle = '#5073e8';
            ctx.arc(point.x, point.y, 6, 0, 2 * Math.PI);
            ctx.fill();
            
            ctx.beginPath();
            ctx.fillStyle = 'white';
            ctx.arc(point.x, point.y, 3, 0, 2 * Math.PI);
            ctx.fill();
        });
        
        // Заголовок
        ctx.fillStyle = '#1e1f4b';
        ctx.font = 'bold 24px sans-serif';
        ctx.textAlign = 'center';
        ctx.fillText(`Курс ${currency} к BYN`, canvas.width / 2, 35);
        
        // Статистика
        ctx.fillStyle = '#6c757d';
        ctx.font = '14px sans-serif';
        ctx.textAlign = 'right';
        const avgValue = (values.reduce((a, b) => a + b, 0) / values.length).toFixed(4);
        const minVal = Math.min(...values).toFixed(4);
        const maxVal = Math.max(...values).toFixed(4);
        ctx.fillText(`Мін: ${minVal} Br  |  Макс: ${maxVal} Br  |  Средн: ${avgValue} Br`, 
            canvas.width - padding.right, canvas.height - 15);
        
        // Интерактивность
        let hoveredPoint = null;
        
        canvas.addEventListener('mousemove', function(e) {
            const rect = canvas.getBoundingClientRect();
            const mouseX = e.clientX - rect.left;
            const mouseY = e.clientY - rect.top;
            
            // Находим ближайшую точку
            let closest = null;
            let minDistance = 50;
            
            points.forEach(point => {
                const distance = Math.sqrt(
                    Math.pow(mouseX - point.x, 2) + 
                    Math.pow(mouseY - point.y, 2)
                );
                
                if (distance < minDistance) {
                    minDistance = distance;
                    closest = point;
                }
            });
            
            if (closest && closest !== hoveredPoint) {
                hoveredPoint = closest;
                canvas.style.cursor = 'pointer';
                
                // Показываем tooltip
                tooltip.innerHTML = `
                    <div style="font-weight: 700; margin-bottom: 0.25rem;">${closest.value.toFixed(4)} Br</div>
                    <div style="font-size: 0.75rem; opacity: 0.8;">День ${closest.index + 1}</div>
                `;
                tooltip.style.left = (closest.x - 40) + 'px';
                tooltip.style.top = (closest.y - 70) + 'px';
                tooltip.classList.add('show');
                
                // Перерисовываем с подсветкой
                redrawWithHighlight(closest, points, data, currency, ctx, canvas, padding, chartWidth, chartHeight, minValue, valueRange, gradient);
            } else if (!closest && hoveredPoint) {
                hoveredPoint = null;
                canvas.style.cursor = 'default';
                tooltip.classList.remove('show');
            }
        });
        
        canvas.addEventListener('mouseleave', function() {
            hoveredPoint = null;
            canvas.style.cursor = 'default';
            tooltip.classList.remove('show');
        });
        
        function redrawWithHighlight(highlightPoint, points, data, currency, ctx, canvas, padding, chartWidth, chartHeight, minValue, valueRange, gradient) {
            ctx.clearRect(0, 0, canvas.width, canvas.height);
            
            // Фон
            ctx.fillStyle = '#fafbfc';
            ctx.fillRect(padding.left, padding.top, chartWidth, chartHeight);
            
            // Сетка
            ctx.strokeStyle = '#e1e8ed';
            ctx.lineWidth = 1;
            for (let i = 0; i <= 6; i++) {
                const y = padding.top + (chartHeight / 6) * i;
                ctx.beginPath();
                ctx.moveTo(padding.left, y);
                ctx.lineTo(canvas.width - padding.right, y);
                ctx.stroke();
            }
            
            // Рамка
            ctx.strokeStyle = '#d1d5db';
            ctx.lineWidth = 2;
            ctx.strokeRect(padding.left, padding.top, chartWidth, chartHeight);
            
            // Градиент
            ctx.fillStyle = gradient;
            ctx.beginPath();
            points.forEach((point, index) => {
                if (index === 0) {
                    ctx.moveTo(point.x, canvas.height - padding.bottom);
                    ctx.lineTo(point.x, point.y);
                } else {
                    ctx.lineTo(point.x, point.y);
                }
            });
            ctx.lineTo(canvas.width - padding.right, canvas.height - padding.bottom);
            ctx.closePath();
            ctx.fill();
            
            // Линия
            ctx.strokeStyle = '#5073e8';
            ctx.lineWidth = 3;
            ctx.beginPath();
            points.forEach((point, index) => {
                if (index === 0) ctx.moveTo(point.x, point.y);
                else ctx.lineTo(point.x, point.y);
            });
            ctx.stroke();
            
            // Вертикальная линия от точки
            ctx.strokeStyle = 'rgba(80, 115, 232, 0.3)';
            ctx.lineWidth = 2;
            ctx.setLineDash([5, 5]);
            ctx.beginPath();
            ctx.moveTo(highlightPoint.x, highlightPoint.y);
            ctx.lineTo(highlightPoint.x, canvas.height - padding.bottom);
            ctx.stroke();
            ctx.setLineDash([]);
            
            // Точки
            points.forEach(point => {
                const isHighlighted = point === highlightPoint;
                
                ctx.beginPath();
                ctx.fillStyle = isHighlighted ? '#ff6b6b' : '#5073e8';
                ctx.arc(point.x, point.y, isHighlighted ? 8 : 6, 0, 2 * Math.PI);
                ctx.fill();
                
                ctx.beginPath();
                ctx.fillStyle = 'white';
                ctx.arc(point.x, point.y, isHighlighted ? 4 : 3, 0, 2 * Math.PI);
                ctx.fill();
            });
            
            // Заголовок
            ctx.fillStyle = '#1e1f4b';
            ctx.font = 'bold 24px sans-serif';
            ctx.textAlign = 'center';
            ctx.fillText(`Курс ${currency} к BYN`, canvas.width / 2, 35);
        }
    }
});
