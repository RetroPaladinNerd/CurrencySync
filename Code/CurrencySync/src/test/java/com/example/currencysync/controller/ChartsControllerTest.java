package com.example.currencysync.controller;

import com.example.currencysync.model.Currency;
import com.example.currencysync.service.CurrencyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChartsController.class)
class ChartsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrencyService currencyService;

    @Test
    void testChartsPage() throws Exception {
        List<Currency> currencies = new ArrayList<>();
        when(currencyService.getAllRates()).thenReturn(currencies);

        mockMvc.perform(get("/charts"))
                .andExpect(status().isOk())
                .andExpect(view().name("charts"))
                .andExpect(model().attributeExists("currencies"));
    }

    @Test
    void testGetChartData() throws Exception {
        Currency currency = new Currency();
        currency.setCharCode("USD");
        currency.setNominal(1);
        currency.setValue(3.20);
        currency.setVunitRate(3.20);

        List<Currency> chartData = List.of(currency);
        when(currencyService.getRateHistoryInBYN(anyString(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(chartData);

        mockMvc.perform(get("/charts/data")
                .param("currency", "USD")
                .param("from", "2025-10-01")
                .param("to", "2025-10-30"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].value").value(3.20));
    }
}
