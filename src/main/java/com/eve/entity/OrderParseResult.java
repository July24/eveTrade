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
    //可能为0
    int inventory = 0;
    boolean monopoly = false;
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

    public void newComputerProfit(float volumn) {
        if(volRemain == 0) {
            monopoly = true;
            return;
        }
        double jitaSell = Double.parseDouble(eveMarketData.getSell().getMin());
        if(jitaSell == 0) {
            return;
        }
        double profit =
                (minPrice - jitaSell - PrjConst.EXPRESS_FAX_CUBIC_METRES * volumn) * (1 - PrjConst.AVG_BROKER_FAX) * (1 - PrjConst.SELL_FAX);
        BigDecimal round = NumberUtil.round(profit, 2);
        statisticData.setProfit(round.intValue());
        double margin = NumberUtil.div(round.doubleValue(), jitaSell, 2);
        statisticData.setProfitMargin(margin);
    }

    public void computerProfit() {
        if(volRemain == 0) {
            monopoly = true;
            return;
        }
        double jitaSell = Double.parseDouble(eveMarketData.getSell().getMin());
        if(jitaSell == 0) {
            return;
        }
        if(item == null) {
            return;
        }
        double profit =
                (minPrice - jitaSell - PrjConst.EXPRESS_FAX_CUBIC_METRES * item.getVolumn()) * (1 - PrjConst.AVG_BROKER_FAX) * (1 - PrjConst.SELL_FAX);
        BigDecimal round = NumberUtil.round(profit, 2);
        statisticData.setProfit(round.intValue());
        double margin = NumberUtil.div(round.doubleValue(), jitaSell, 2);
        statisticData.setProfitMargin(margin);
    }
    //    理想销售量 = 7 * daily
//            理想进货量 =
//            7*daily>x
//		7*daily-x
//	7*daily<=x
//		max(3*daily(175/x), daily)
//    实际进货量 = 理想进货量 - rf库存
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

//
//        int hopeCount = NumberUtil.round(7 * dailySalesVolume, 0).intValue();
//        if(hopeCount > volRemain) {
//            recommendedCount = hopeCount - inventory;
//        } else {
//            BigDecimal mul = NumberUtil.div(new BigDecimal(hopeCount),
//                    new BigDecimal(2));
//            recommendedCount = NumberUtil.round(NumberUtil.sub(mul,
//                    new BigDecimal(inventory)), 0).intValue();
//        }





//        if(hopeCount > volRemain) {
//            recommendedCount = hopeCount - volRemain - inventory;
//        } else {
//            double rate = NumberUtil.div(hopeCount, volRemain);
//            BigDecimal predict = NumberUtil.mul(3, dailySalesVolume, rate);
//            recommendedCount = NumberUtil.max(predict, NumberUtil.round(dailySalesVolume,0)).subtract(new BigDecimal(inventory)).intValue();
//        }
    }

    public void predictMonoPurchaseCount() {
        if(dailySalesVolume < 1) {
            recommendedCount = 7 - inventory;
        } else {
            recommendedCount = NumberUtil.round(7 * dailySalesVolume - inventory, 0).intValue();
        }
    }
}
