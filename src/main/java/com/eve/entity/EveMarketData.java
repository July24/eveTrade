package com.eve.entity;

import lombok.Data;

@Data
public class EveMarketData {
    private EveMarketBuyOrder buy;
    private EveMarketSellOrder sell;
}
