package com.example.currencysync.controller;

import com.example.currencysync.dto.ConversionResult;
import com.example.currencysync.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/converter")
public class ConverterController {

    private final CurrencyService currencyService;

    @GetMapping
    public String converterPage(Model model) {
        model.addAttribute("currencies", currencyService.getAllRates());
        return "converter";
    }

    @PostMapping("/convert")
    @ResponseBody
    public ConversionResult convert(
            @RequestParam double amount,
            @RequestParam String from,
            @RequestParam String to) {
        return currencyService.convert(amount, from, to);
    }
}
