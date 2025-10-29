document.addEventListener('DOMContentLoaded', function() {
    const convertForm = document.getElementById('convertForm');
    const swapButton = document.getElementById('swapButton');
    const resultDiv = document.getElementById('conversionResult');
    const resultAmountInput = document.getElementById('resultAmount');
    const amountInput = document.getElementById('amount');
    const fromCurrencySelect = document.getElementById('fromCurrency');
    const toCurrencySelect = document.getElementById('toCurrency');
    
    // Автоматическая конвертация при изменении значений
    if (amountInput) {
        amountInput.addEventListener('input', autoConvert);
    }
    
    if (fromCurrencySelect) {
        fromCurrencySelect.addEventListener('change', autoConvert);
    }
    
    if (toCurrencySelect) {
        toCurrencySelect.addEventListener('change', autoConvert);
    }
    
    // Обработка формы
    if (convertForm) {
        convertForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            await performConversion();
        });
    }
    
    // Кнопка обмена валют
    if (swapButton) {
        swapButton.addEventListener('click', function() {
            const fromValue = fromCurrencySelect.value;
            const toValue = toCurrencySelect.value;
            
            fromCurrencySelect.value = toValue;
            toCurrencySelect.value = fromValue;
            
            // Меняем местами суммы
            const amount = amountInput.value;
            const result = resultAmountInput.value;
            
            if (result) {
                amountInput.value = result;
                performConversion();
            }
        });
    }
    
    async function autoConvert() {
        const amount = amountInput.value;
        if (amount && amount > 0) {
            await performConversion();
        }
    }
    
    async function performConversion() {
        const amount = amountInput.value;
        const fromCurrency = fromCurrencySelect.value;
        const toCurrency = toCurrencySelect.value;
        
        if (!amount || amount <= 0) {
            return;
        }
        
        try {
            const response = await fetch('/converter/convert', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: `amount=${amount}&from=${fromCurrency}&to=${toCurrency}`
            });
            
            if (!response.ok) {
                throw new Error('Ошибка при конвертации');
            }
            
            const data = await response.json();
            displayResult(data);
        } catch (error) {
            console.error('Conversion error:', error);
            if (resultDiv) {
                resultDiv.innerHTML = '<p style="color: #e53e3e;">Ошибка при конвертации валюты. Попробуйте позже.</p>';
                resultDiv.classList.add('show');
            }
        }
    }
    
    function displayResult(data) {
        // Обновляем поле результата
        if (resultAmountInput) {
            resultAmountInput.value = data.result.toFixed(2);
        }
        
        // Показываем детальную информацию
        if (resultDiv) {
            const rate = (data.result / data.amount).toFixed(4);
            const inverseRate = (data.amount / data.result).toFixed(4);
            
            const resultHTML = `
                <div class="result-amount">
                    ${formatNumber(data.amount)} ${data.fromCurrency} = 
                    ${formatNumber(data.result)} ${data.toCurrency}
                </div>
                <div class="result-rate">
                    1 ${data.fromCurrency} = ${rate} ${data.toCurrency}
                </div>
                <div class="result-rate">
                    1 ${data.toCurrency} = ${inverseRate} ${data.fromCurrency}
                </div>
                <div class="result-rate" style="margin-top: 0.75rem;">
                    📅 Курс от ${formatDate(data.rateDate)}
                </div>
            `;
            
            resultDiv.innerHTML = resultHTML;
            resultDiv.classList.add('show');
        }
    }
    
    function formatNumber(num) {
        return new Intl.NumberFormat('ru-RU', {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        }).format(num);
    }
    
    function formatDate(dateString) {
        const date = new Date(dateString);
        return date.toLocaleDateString('ru-RU', {
            year: 'numeric',
            month: 'long',
            day: 'numeric'
        });
    }
    
    // Табы (если есть)
    const tabButtons = document.querySelectorAll('.tab-button');
    tabButtons.forEach(button => {
        button.addEventListener('click', function() {
            if (!this.hasAttribute('onclick')) {
                tabButtons.forEach(btn => btn.classList.remove('active'));
                this.classList.add('active');
            }
        });
    });
});
