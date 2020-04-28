package com.eve;

import cn.hutool.core.util.NumberUtil;
import com.eve.entity.Fitting;
import com.eve.entity.OrderParseResult;
import com.eve.util.TradeUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LocalMain {

    private TradeUtil tradeUtil = new TradeUtil();

    public static void main(String[] args) throws Exception {
        LocalMain localMain = new LocalMain();
        localMain.parseMarketGetPurchaseList("D:\\yhy\\doc\\eve4.24\\common", null, null, null, 500000, 0.5);
    }

    private void parseMarketGetPurchaseList(String folderPath, String orderListPath, String rfWareHousePath,
                                            String jitaWarehousePath, int hopeProfit, double profitMargin) throws Exception {
        tradeUtil.getOasaMarketRecommendedPurchaseList(folderPath, orderListPath, rfWareHousePath, jitaWarehousePath, hopeProfit, profitMargin);
    }
}
