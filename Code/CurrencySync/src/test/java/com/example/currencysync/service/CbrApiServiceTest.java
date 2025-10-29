package com.example.currencysync.service;

import com.example.currencysync.model.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CbrApiServiceTest {

    @Autowired
    private CbrApiService cbrApiService;

    @Test
    void testGetDynamicRatesReturnsNonNullList() {
        LocalDate from = LocalDate.now().minusDays(7);
        LocalDate to = LocalDate.now();

        List<Currency> result = cbrApiService.getDynamicRates("USD", from, to);

        assertNotNull(result);
    }

    @Test
    void testGetDynamicRatesWithValidDates() {
        LocalDate from = LocalDate.of(2025, 10, 1);
        LocalDate to = LocalDate.of(2025, 10, 30);

        List<Currency> result = cbrApiService.getDynamicRates("USD", from, to);

        assertNotNull(result);
    }
}
