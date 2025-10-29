package com.example.currencysync.service;

import com.example.currencysync.dto.ConversionResult;
import com.example.currencysync.model.Currency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class CurrencyServiceTest {

    @Mock
    private CbrApiService cbrApiService;

    @InjectMocks
    private CurrencyService currencyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testConvertBYNtoUSD() {
        ConversionResult result = currencyService.convert(1000, "BYN", "USD");
        
        assertNotNull(result);
        assertEquals(1000, result.getAmount());
        assertEquals("BYN", result.getFromCurrency());
        assertEquals("USD", result.getToCurrency());
        assertTrue(result.getResult() > 0);
    }

    @Test
    void testConvertUSDtoBYN() {
        ConversionResult result = currencyService.convert(100, "USD", "BYN");
        
        assertNotNull(result);
        assertEquals(100, result.getAmount());
        assertEquals("USD", result.getFromCurrency());
        assertEquals("BYN", result.getToCurrency());
        assertTrue(result.getResult() > 0);
    }

    @Test
    void testGetAllRates() {
        List<Currency> rates = currencyService.getAllRates();
        
        assertNotNull(rates);
        assertFalse(rates.isEmpty());
        
        assertTrue(rates.stream().anyMatch(c -> c.getCharCode().equals("USD")));
        assertTrue(rates.stream().anyMatch(c -> c.getCharCode().equals("EUR")));
        
        assertFalse(rates.stream().anyMatch(c -> c.getCharCode().equals("BYN")));
    }

    @Test
    void testGetRateHistoryInBYN() {
        Currency rubCurrency = new Currency();
        rubCurrency.setCharCode("USD");
        rubCurrency.setName("Доллар США");
        rubCurrency.setNominal(1);
        rubCurrency.setValue(96.0);
        rubCurrency.setVunitRate(96.0);
        
        when(cbrApiService.getDynamicRates(anyString(), any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(List.of(rubCurrency));
        
        LocalDate from = LocalDate.now().minusDays(7);
        LocalDate to = LocalDate.now();
        
        List<Currency> result = currencyService.getRateHistoryInBYN("USD", from, to);
        
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
}
