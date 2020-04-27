package com.eve.entity;

import com.eve.entity.database.Items;
import lombok.Data;

@Data
public class OrderParseResult {
    //可能为0
    double minPrice;
    //可能为0
    double maxPrice;
    //可能为0
    double average;
    //可能为0
    int volRemain;
    int typeID;
    //可能为0
    double dailySalesVolume;
    //可能为0
    int inventory;
    boolean monopoly = false;
    Items item;
    EveMarketData eveMarketData;
    StatisticData statisticData;

    public void addInventory(int count) {
        inventory += count;
    }

    public void minusVolRemain(int count) {
        volRemain -= count;
    }
}
