package com.example.currencysync.controller;

import com.example.currencysync.dto.ConversionResult;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConverterController.class)
class ConverterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CurrencyService currencyService;

    @Test
    void testConverterPage() throws Exception {
        List<Currency> currencies = new ArrayList<>();
        when(currencyService.getAllRates()).thenReturn(currencies);

        mockMvc.perform(get("/converter"))
                .andExpect(status().isOk())
                .andExpect(view().name("converter"))
                .andExpect(model().attributeExists("currencies"));
    }

    @Test
    void testConvertEndpoint() throws Exception {
        ConversionResult result = new ConversionResult(
            1000.0,
            "BYN",
            "USD",
            312.50,
            1.0,
            3.20,
            LocalDate.now()
        );

        when(currencyService.convert(anyDouble(), anyString(), anyString()))
                .thenReturn(result);

        mockMvc.perform(post("/converter/convert")
                .param("amount", "1000")
                .param("from", "BYN")
                .param("to", "USD")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.amount").value(1000.0))
                .andExpect(jsonPath("$.fromCurrency").value("BYN"))
                .andExpect(jsonPath("$.toCurrency").value("USD"))
                .andExpect(jsonPath("$.result").value(312.50));
    }
}
