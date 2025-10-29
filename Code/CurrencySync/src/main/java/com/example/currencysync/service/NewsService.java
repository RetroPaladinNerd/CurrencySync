package com.example.currencysync.service;

import com.example.currencysync.model.NewsItem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class NewsService {
    
    @Cacheable("currencyNews")
    public List<NewsItem> getCurrencyNews() {
        // В реальном проекте здесь должна быть интеграция с белорусскими новостными API
        // Например, belta.by, naviny.by, finance.tut.by
        List<NewsItem> news = new ArrayList<>();
        
        news.add(new NewsItem(
            "Белорусский рубль укрепился к доллару США",
            "Национальный банк Беларуси установил официальный курс белорусского рубля к доллару США на уровне 3.20 руб.",
            "БЕЛТА",
            LocalDateTime.now().minusHours(2),
            "https://belta.by",
            null
        ));
        
        news.add(new NewsItem(
            "Курс евро к белорусскому рублю вырос",
            "Официальный курс евро к белорусскому рублю установлен на уровне 3.45 рублей",
            "Naviny.by",
            LocalDateTime.now().minusHours(5),
            "https://naviny.by",
            null
        ));
        
        news.add(new NewsItem(
            "Прогноз курсов валют на следующую неделю",
            "Эксперты дали прогнозы по основным валютным парам на предстоящую неделю",
            "TUT.BY",
            LocalDateTime.now().minusHours(8),
            "https://finance.tut.by",
            null
        ));
        
        news.add(new NewsItem(
            "Российский рубль торгуется стабильно",
            "Курс российского рубля к белорусскому рублю остается на стабильном уровне",
            "БелТА",
            LocalDateTime.now().minusHours(12),
            "https://belta.by",
            null
        ));
        
        news.add(new NewsItem(
            "Аналитики прогнозируют укрепление доллара",
            "Международные эксперты ожидают укрепления доллара США в ближайшие месяцы",
            "Naviny.by",
            LocalDateTime.now().minusHours(24),
            "https://naviny.by",
            null
        ));
        
        return news;
    }
}
