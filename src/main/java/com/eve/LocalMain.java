package com.eve;

import cn.hutool.core.util.NumberUtil;
import com.eve.entity.Fitting;
import com.eve.entity.OrderParseResult;
import com.eve.util.TradeUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LocalMain {
    //TODO 找到订单列表中不是最低价的订单
    private TradeUtil tradeUtil = new TradeUtil();

    public static void main(String[] args) throws Exception {
        HashMap<Integer, Integer> map = new HashMap<>();
        System.out.println(map.get(12));
//        LocalMain localMain = new LocalMain();
//        localMain.parseMarketGetPurchaseList();
    }


    /**
     * 从推荐列表中得到吉他未购买的剩余物品列表
     * 输入
     *  result/trade/simple 存在购买列表
     *  orderFilePath 订单路径
     *  jita库存放入
     *      result/trade/inventory/jitaInventory
     *  输出
     *      result/trade/recommendNotBuyList
     */
    private void getRecommendNotBuyList() throws Exception {
//        String orderFilePath = "C:\\Users\\叶皓宇\\Documents\\EVE\\logs" +
//                "\\Marketlogs\\My Orders-2020.04.30 1205.txt";
        tradeUtil.getRecommendNotBuyList(null);
    }
}
