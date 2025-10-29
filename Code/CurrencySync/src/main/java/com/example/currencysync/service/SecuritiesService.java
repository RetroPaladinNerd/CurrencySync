package com.example.currencysync.service;

import com.example.currencysync.model.Security;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
@Slf4j
public class SecuritiesService {
    
    private final Random random = new Random();
    
    @Cacheable("securities")
    public List<Security> getSecurities() {
        // В реальном проекте здесь должна быть интеграция с реальными биржевыми API
        // Например, Yahoo Finance API, Alpha Vantage, IEX Cloud
        List<Security> securities = new ArrayList<>();
        
        securities.add(createSecurity("AAPL", "Apple Inc.", "APPLE", 178.50));
        securities.add(createSecurity("MSFT", "Microsoft", "MICROSOFT", 372.80));
        securities.add(createSecurity("GOOGL", "Alphabet Inc.", "ALPHABET", 142.30));
        securities.add(createSecurity("AMZN", "Amazon", "AMAZON", 145.90));
        securities.add(createSecurity("TSLA", "Tesla Inc.", "TESLA", 242.15));
        securities.add(createSecurity("NVDA", "NVIDIA", "NVIDIA", 485.60));
        securities.add(createSecurity("META", "Meta Platforms", "META", 312.45));
        securities.add(createSecurity("NFLX", "Netflix", "NETFLIX", 425.80));
        
        return securities;
    }
    
    private Security createSecurity(String secId, String shortName, String latName, double basePrice) {
        double changePercent = -5.0 + (random.nextDouble() * 10.0);
        double lastPrice = basePrice * (1 + changePercent / 100);
        
        return new Security(
            secId,
            shortName,
            latName,
            Math.round(lastPrice * 100.0) / 100.0,
            Math.round(changePercent * 100.0) / 100.0,
            LocalDateTime.now()
        );
    }
    
}
