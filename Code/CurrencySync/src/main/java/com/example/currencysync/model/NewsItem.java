package com.example.currencysync.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsItem {
    private String title;
    private String description;
    private String source;
    private LocalDateTime publishedAt;
    private String url;
    private String imageUrl;
}
