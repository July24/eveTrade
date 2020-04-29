package com.eve.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class EveMarketData implements Serializable {
    private EveMarketBuyOrder buy;
    private EveMarketSellOrder sell;
}
