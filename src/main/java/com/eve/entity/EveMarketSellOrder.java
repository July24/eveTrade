package com.eve.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class EveMarketSellOrder implements Serializable {
    private EveMarketForQuery forQuery;
    private String volume;
    private String wavg;
    private String avg;
    private String variance;
    private String stdDev;
    private String median;
    private String fivePercent;
    private String max;
    private String min;
    private String highToLow;
    private String generated;
}
