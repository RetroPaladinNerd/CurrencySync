package com.example.currencysync.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Security {
    private String secId;
    private String shortName;
    private String latName;
    private double lastPrice;
    private double changePercent;
    private LocalDateTime lastUpdate;
}
