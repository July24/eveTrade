package com.eve.entity;

import cn.hutool.core.util.NumberUtil;
import com.eve.entity.database.Items;
import com.eve.util.PrjConst;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class OrderParseResult implements Serializable {
    //可能为0
    double minPrice = Long.MAX_VALUE;
    //可能为0
    double maxPrice = 0.0;
    //可能为0
    double average = 0.0;
    //可能为0
    int volRemain = 0;
    int typeID;
    //可能为0
    double dailySalesVolume = 0.0;
    //出口直接到吉他卖的利润
    double profitByBuyOrder = 0.0;
    //可能为0
    int inventory = 0;
    Items item;
    EveMarketData eveMarketData;
    StatisticData statisticData = new StatisticData();
    int recommendedCount = 0;
    JitaData jitaData;

    public void addInventory(int count) {
        inventory += count;
    }

    public void minusVolRemain(int count) {
        volRemain -= count;
    }

    public void computerProfit() {
        if(volRemain == 0) {
            return;
        }
        double jitaSell = eveMarketData.getSell().getMin();
        if(jitaSell == 0) {
            return;
        }
        double profit =
                (minPrice - jitaSell - PrjConst.EXPRESS_FAX_CUBIC_METRES * item.getVolume()) * (1 - PrjConst.AVG_BROKER_FAX) * (1 - PrjConst.SELL_FAX);
        BigDecimal round = NumberUtil.round(profit, 2);
        statisticData.setProfit(round.intValue());
        double margin = NumberUtil.div(round.doubleValue(), jitaSell, 2);
        statisticData.setProfitMargin(margin);
    }

    public void predictPurchaseCount() {
        if(dailySalesVolume < 1) {
            recommendedCount = 0;
            return;
        }
        int hopeCount = NumberUtil.round(7 * dailySalesVolume, 0).intValue();
        BigDecimal mul = NumberUtil.div(new BigDecimal(hopeCount),
                new BigDecimal(2));
        recommendedCount = NumberUtil.round(NumberUtil.sub(mul,
                new BigDecimal(inventory)), 0).intValue();
    }

    public void computeTotalProfit() {
        int profit = statisticData.getProfit();
        statisticData.setTotalProfit(profit * recommendedCount);
    }
}
