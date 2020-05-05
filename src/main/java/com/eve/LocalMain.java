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
        LocalMain localMain = new LocalMain();
//        localMain.parseMarketGetPurchaseList();
        localMain.getInventoryRelist();
    }

    /**
     * 分析市场得到推荐购买量
     * 输入
     *  rf订单路径  orderListPath
     *  期望利润    hopeProfit
     *  期望利润率   profitMargin
     *
     *  需要分析的市场订单/历史
     *      放入result/trade/marketData 文件夹中
     *          包括市场导入的.xml以及产品历史复制到同名的无后缀文件中
     *
     *  rf本地库存放入
     *      result/trade/inventory/rfInventory
     *  吉他本地库存放入
     *      result/trade/inventory/jitaInventory
     *
     * 输出
     *  result/trade/simple 根据期望利润/利润率 建议购买物品及数量
     *  result/trade/local/temp 临时储存本次购买物品信息(用于在jita修正多重购买订单)
     */
    private void parseMarketGetPurchaseList() throws Exception {
//        String orderListPath = "C:\\Users\\叶皓宇\\Documents\\EVE\\logs" +
//                "\\Marketlogs\\My Orders-2020.05.02 0423.txt";
        int hopeProfit = 300000;
        double profitMargin = 0.4;
        tradeUtil.getOasaMarketRecommendedPurchaseList(null, hopeProfit,
                profitMargin);
    }

    /**
     * 修正多重购买订单(与分析市场得到推荐购买量配合使用)
     *      如果输出有内容则复制输出多重购买后继续输入，直到无内容时确认购买
     * 输入
     *  多重购买订单复制后放入
     *      result/trade/revisionBuy/buyOrderOut
     * 输出
     *      result/trade/revisionBuy/revisionBuyOrder
     */
    private void revisionBuyOrder() throws Exception {
        int hopeProfit = 100000;
        double profitMargin = 0.5;
        tradeUtil.revisionBuyOrder(hopeProfit, profitMargin);
    }

    /**
     * 改单时得到仓库里未上架物品
     * 输入
     *  orderFilePath 订单路径
     *  rf本地库存放入
     *      result/trade/inventory/rfInventory
     *  输出
     *      result/trade/inventoryRelist
     */
    private void getInventoryRelist() throws Exception {
        String orderFilePath = "C:\\Users\\叶皓宇\\Documents\\EVE\\logs\\Marketlogs\\My Orders-2020.05.05 0741.txt";
        tradeUtil.getInventoryRelist(orderFilePath);
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
