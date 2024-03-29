package com.eve.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class EveMarketBuyOrder implements Serializable {
    private EveMarketForQuery forQuery;
    private String volume;
    private String wavg;
    private String avg;
    private String variance;
    private String stdDev;
    private String median;
    private String fivePercent;
    private double max;
    private double min;
    private String highToLow;
    private String generated;
}
