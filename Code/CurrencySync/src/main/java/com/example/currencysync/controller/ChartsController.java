package com.example.currencysync.controller;

import com.example.currencysync.model.Currency;
import com.example.currencysync.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/charts")
@RequiredArgsConstructor
public class ChartsController {
    
    private final CurrencyService currencyService;
    
    @GetMapping
    public String charts(Model model) {
        List<Currency> currencies = currencyService.getAllRates();
        model.addAttribute("currencies", currencies);
        return "charts";
    }
    
    @GetMapping("/data")
    @ResponseBody
    public List<Map<String, Object>> getChartData(
            @RequestParam String currency,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        
        List<Currency> rates = currencyService.getRateHistoryInBYN(currency, from, to);
        
        return rates.stream()
            .map(rate -> {
                Map<String, Object> point = new HashMap<>();
                point.put("value", rate.getVunitRate());
                point.put("date", rate.getDate());
                return point;
            })
            .collect(Collectors.toList());
    }
}
