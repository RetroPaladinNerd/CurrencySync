package com.example.currencysync.service;

import com.example.currencysync.dto.ConversionResult;
import com.example.currencysync.model.Currency;
import com.example.currencysync.model.ExchangeRate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CurrencyService {
    
    private final CbrApiService cbrApiService;
    
    // Курсы валют к BYN (белорусский рубль)
    private static final Map<String, Double> BYN_RATES = new HashMap<>();
    
    static {
        BYN_RATES.put("BYN", 1.0);
        BYN_RATES.put("USD", 3.20);
        BYN_RATES.put("EUR", 3.45);
        BYN_RATES.put("RUB", 0.033);
        BYN_RATES.put("GBP", 4.05);
        BYN_RATES.put("JPY", 0.021);
        BYN_RATES.put("CNY", 0.44);
        BYN_RATES.put("CHF", 3.65);
        BYN_RATES.put("AUD", 2.08);
        BYN_RATES.put("CAD", 2.32);
        BYN_RATES.put("PLN", 0.78);
        BYN_RATES.put("UAH", 0.079);
        BYN_RATES.put("KZT", 0.0065);
        BYN_RATES.put("SEK", 0.30);
        BYN_RATES.put("NOK", 0.29);
        BYN_RATES.put("DKK", 0.46);
        BYN_RATES.put("TRY", 0.093);
        BYN_RATES.put("INR", 0.038);
        BYN_RATES.put("BRL", 0.64);
        BYN_RATES.put("MXN", 0.18);
        BYN_RATES.put("ZAR", 0.17);
        BYN_RATES.put("SGD", 2.38);
        BYN_RATES.put("HKD", 0.41);
        BYN_RATES.put("KRW", 0.0024);
        BYN_RATES.put("THB", 0.091);
        BYN_RATES.put("MYR", 0.70);
        BYN_RATES.put("IDR", 0.00020);
        BYN_RATES.put("PHP", 0.055);
        BYN_RATES.put("CZK", 0.14);
        BYN_RATES.put("HUF", 0.0084);
        BYN_RATES.put("RON", 0.68);
        BYN_RATES.put("BGN", 1.76);
        BYN_RATES.put("ILS", 0.85);
        BYN_RATES.put("AED", 0.87);
        BYN_RATES.put("SAR", 0.85);
    }
    
    public ConversionResult convert(double amount, String fromCurrency, String toCurrency) {
        double fromRate = BYN_RATES.getOrDefault(fromCurrency, 1.0);
        double toRate = BYN_RATES.getOrDefault(toCurrency, 1.0);
        
        double amountInBYN = amount * fromRate;
        double result = amountInBYN / toRate;
        
        return new ConversionResult(
            amount,
            fromCurrency,
            toCurrency,
            result,
            fromRate,
            toRate,
            LocalDate.now()
        );
    }
    
    public List<Currency> getAllRates() {
        return BYN_RATES.entrySet().stream()
            .filter(entry -> !entry.getKey().equals("BYN"))
            .map(entry -> {
                Currency currency = new Currency();
                currency.setNumCode(entry.getKey());
                currency.setCharCode(entry.getKey());
                currency.setNominal(1);
                currency.setName(getCurrencyName(entry.getKey()));
                currency.setValue(entry.getValue());
                currency.setVunitRate(entry.getValue());
                return currency;
            })
            .sorted((a, b) -> a.getCharCode().compareTo(b.getCharCode()))
            .toList();
    }
    
    public List<Currency> getRateHistory(String currencyCode, LocalDate from, LocalDate to) {
        return cbrApiService.getDynamicRates(currencyCode, from, to);
    }
    
    public List<Currency> getRateHistoryInBYN(String currencyCode, LocalDate from, LocalDate to) {
        List<Currency> rubRates = cbrApiService.getDynamicRates(currencyCode, from, to);
        
        // Конвертируем из RUB в BYN
        double rubToByn = 0.033;
        
        return rubRates.stream()
            .map(currency -> {
                Currency bynCurrency = new Currency();
                bynCurrency.setNumCode(currency.getNumCode());
                bynCurrency.setCharCode(currency.getCharCode());
                bynCurrency.setNominal(currency.getNominal());
                bynCurrency.setName(currency.getName());
                bynCurrency.setValue(currency.getValue() * rubToByn);
                bynCurrency.setVunitRate(currency.getVunitRate() * rubToByn);
                return bynCurrency;
            })
            .collect(Collectors.toList());
    }
    
    private String getCurrencyName(String code) {
        Map<String, String> names = Map.ofEntries(
            Map.entry("USD", "Доллар США"),
            Map.entry("EUR", "Евро"),
            Map.entry("RUB", "Российский рубль"),
            Map.entry("GBP", "Фунт стерлингов"),
            Map.entry("JPY", "Японская иена"),
            Map.entry("CNY", "Китайский юань"),
            Map.entry("CHF", "Швейцарский франк"),
            Map.entry("AUD", "Австралийский доллар"),
            Map.entry("CAD", "Канадский доллар"),
            Map.entry("PLN", "Польский злотый"),
            Map.entry("UAH", "Украинская гривна"),
            Map.entry("KZT", "Казахстанский тенге"),
            Map.entry("SEK", "Шведская крона"),
            Map.entry("NOK", "Норвежская крона"),
            Map.entry("DKK", "Датская крона"),
            Map.entry("TRY", "Турецкая лира"),
            Map.entry("INR", "Индийская рупия"),
            Map.entry("BRL", "Бразильский реал"),
            Map.entry("MXN", "Мексиканский песо"),
            Map.entry("ZAR", "Южноафриканский рэнд"),
            Map.entry("SGD", "Сингапурский доллар"),
            Map.entry("HKD", "Гонконгский доллар"),
            Map.entry("KRW", "Южнокорейская вона"),
            Map.entry("THB", "Тайский бат"),
            Map.entry("MYR", "Малайзийский ринггит"),
            Map.entry("IDR", "Индонезийская рупия"),
            Map.entry("PHP", "Филиппинский песо"),
            Map.entry("CZK", "Чешская крона"),
            Map.entry("HUF", "Венгерский форинт"),
            Map.entry("RON", "Румынский лей"),
            Map.entry("BGN", "Болгарский лев"),
            Map.entry("ILS", "Израильский шекель"),
            Map.entry("AED", "Дирхам ОАЭ"),
            Map.entry("SAR", "Саудовский риал")
        );
        return names.getOrDefault(code, code);
    }
}
