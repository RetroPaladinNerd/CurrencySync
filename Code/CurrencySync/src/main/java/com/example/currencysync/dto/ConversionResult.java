package com.example.currencysync.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversionResult {
    private double amount;
    private String fromCurrency;
    private String toCurrency;
    private double result;
    private double fromRate;
    private double toRate;
    private LocalDate rateDate;
}
