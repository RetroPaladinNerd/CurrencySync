package com.example.currencysync.controller;

import com.example.currencysync.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/news")
public class NewsController {

    private final NewsService newsService;

    @GetMapping
    public String newsPage(Model model) {
        model.addAttribute("news", newsService.getCurrencyNews());
        return "news";
    }
}
