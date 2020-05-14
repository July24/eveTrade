package com.eve.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class StatisticData implements Serializable {
    private int profit;
    private double profitMargin;
    private int totalProfit;
}
