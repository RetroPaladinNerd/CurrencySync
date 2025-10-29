package com.example.currencysync.service;

import com.example.currencysync.model.Currency;
import com.example.currencysync.model.ExchangeRate;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class CbrApiService {

    private static final String CBR_DAILY_URL = "https://www.cbr.ru/scripts/XML_daily.asp";
    private static final String CBR_DYNAMIC_URL = "https://www.cbr.ru/scripts/XML_dynamic.asp";

    @Cacheable(value = "dailyRates", unless = "#result == null")
    public ExchangeRate getDailyRates(LocalDate date) {
        try {
            String dateParam = date != null ?
                    "?date_req=" + date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";

            URL url = new URL(CBR_DAILY_URL + dateParam);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            try (InputStream inputStream = connection.getInputStream()) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(inputStream);

                Element root = doc.getDocumentElement();
                String dateStr = root.getAttribute("Date");
                String name = root.getAttribute("name");

                LocalDate ratesDate = LocalDate.parse(dateStr,
                        DateTimeFormatter.ofPattern("dd.MM.yyyy"));

                NodeList valuteNodes = doc.getElementsByTagName("Valute");
                List<Currency> currencies = new ArrayList<>();

                for (int i = 0; i < valuteNodes.getLength(); i++) {
                    Element valute = (Element) valuteNodes.item(i);

                    Currency currency = new Currency();
                    currency.setId(valute.getAttribute("ID"));
                    currency.setNumCode(getElementValue(valute, "NumCode"));
                    currency.setCharCode(getElementValue(valute, "CharCode"));
                    currency.setNominal(Integer.parseInt(getElementValue(valute, "Nominal")));
                    currency.setName(getElementValue(valute, "Name"));
                    currency.setValue(parseDouble(getElementValue(valute, "Value")));
                    currency.setVunitRate(parseDouble(getElementValue(valute, "VunitRate")));

                    currencies.add(currency);
                }

                return new ExchangeRate(ratesDate, name, currencies);
            }

        } catch (Exception e) {
            log.error("Error fetching daily rates from CBR", e);
            return null;
        }
    }

    @Cacheable(value = "dynamicRates")
    public List<Currency> getDynamicRates(String currencyCode, LocalDate dateFrom, LocalDate dateTo) {
        try {
            String url = String.format("%s?date_req1=%s&date_req2=%s&VAL_NM_RQ=%s",
                    CBR_DYNAMIC_URL,
                    dateFrom.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    dateTo.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    getCurrencyId(currencyCode));

            URL apiUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) apiUrl.openConnection();
            connection.setRequestMethod("GET");

            try (InputStream inputStream = connection.getInputStream()) {
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(inputStream);

                NodeList recordNodes = doc.getElementsByTagName("Record");
                List<Currency> rates = new ArrayList<>();

                for (int i = 0; i < recordNodes.getLength(); i++) {
                    Element record = (Element) recordNodes.item(i);

                    Currency currency = new Currency();
                    currency.setCharCode(currencyCode);
                    currency.setNominal(Integer.parseInt(getElementValue(record, "Nominal")));
                    currency.setValue(parseDouble(getElementValue(record, "Value")));
                    currency.setVunitRate(parseDouble(getElementValue(record, "VunitRate")));

                    rates.add(currency);
                }

                return rates;
            }

        } catch (Exception e) {
            log.error("Error fetching dynamic rates from CBR", e);
            return new ArrayList<>();
        }
    }

    private String getElementValue(Element parent, String tagName) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return "";
    }

    private double parseDouble(String value) {
        return Double.parseDouble(value.replace(",", "."));
    }

    private String getCurrencyId(String charCode) {
        return switch (charCode) {
            case "USD" -> "R01235";
            case "EUR" -> "R01239";
            case "GBP" -> "R01035";
            case "JPY" -> "R01820";
            case "CNY" -> "R01375";
            case "CHF" -> "R01775";
            case "AUD" -> "R01010";
            case "CAD" -> "R01350";
            default -> "R01235";
        };
    }
}
