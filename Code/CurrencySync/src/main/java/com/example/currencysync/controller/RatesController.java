package com.example.currencysync.controller;

import com.example.currencysync.model.Security;
import com.example.currencysync.service.CurrencyService;
import com.example.currencysync.service.SecuritiesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/rates")
public class RatesController {

    private final CurrencyService currencyService;
    private final SecuritiesService securitiesService;

    @GetMapping
    public String ratesPage(Model model) {
        model.addAttribute("currencies", currencyService.getAllRates());

        List<Security> securities = securitiesService.getSecurities();
        model.addAttribute("securities", securities);

        return "rates";
    }
}
