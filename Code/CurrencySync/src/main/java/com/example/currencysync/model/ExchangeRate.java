package com.example.currencysync.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRate {
    private LocalDate date;
    private String name;
    private List<Currency> currencies;
}
