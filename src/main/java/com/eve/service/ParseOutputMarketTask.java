package com.eve.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.eve.entity.*;
import com.eve.entity.database.Items;
import com.eve.util.PrjConst;
import com.eve.util.TradeUtil;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.RecursiveTask;

public class ParseOutputMarketTask extends RecursiveTask<Map<Integer, OrderParseResult>> {
    int THRESHOLD = 30;
    Map<Integer, List<EveOrder>> orderMap;
    Map<Integer, Items> itemMap;

    public ParseOutputMarketTask(Map<Integer, List<EveOrder>> orderMap, Map<Integer, Items> itemMap) {
        this.orderMap = orderMap;
        this.itemMap = itemMap;
    }

    @Override
    protected Map<Integer, OrderParseResult> compute() {
        if (orderMap.size() <= THRESHOLD) {
            Map<Integer, OrderParseResult> result = new HashMap<>();
            inputRfOrder(result, orderMap);
            queryJitaOrderInfoFromEsi(result);
            try {
                getRecommendedPurchaseQuantity(result);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        } else {
            HashMap<Integer, List<EveOrder>> leftMap = new HashMap<>();
            HashMap<Integer, List<EveOrder>> rightMap = new HashMap<>();
            splitMap(leftMap, rightMap);
            ParseOutputMarketTask left = new ParseOutputMarketTask(leftMap, itemMap);
            ParseOutputMarketTask right = new ParseOutputMarketTask(rightMap, itemMap);
            invokeAll(left, right);
            Map<Integer, OrderParseResult> merge = left.join();
            merge.putAll(right.join());
            return merge;
        }
    }

    private static void testJitaOrderXml() {
        List<Integer> typeIDs = new ArrayList<>();
        typeIDs.add(17938);
        typeIDs.add(1195);
        typeIDs.add(1201);
//        List<Integer> typeIDs = new ArrayList<>(orderResult.keySet());
        for (int i = 0; i < typeIDs.size(); i = i + 200) {
            int end = NumberUtil.min(i + 200, typeIDs.size());
            List<Integer> sub = typeIDs.subList(i, end);

            HashMap<String, Object> paramMap = new HashMap<>();
            paramMap.put("typeid", listToString(sub));
//            paramMap.put("usesystem", PrjConst.SOLAR_SYSTEM_ID_JITA);
            String result= HttpUtil.get("https://api.evemarketer" +
                    ".com/ec/marketstat", paramMap);
            System.out.println(result);
//            List<EveMarketData> array = JSON.parseArray(result, EveMarketData.class);
//            for(EveMarketData data : array) {
//                OrderParseResult orderParseResult = orderResult.get(Integer.parseInt(data.getBuy().getForQuery().getTypes().get(0)));
//                orderParseResult.setEveMarketData(data);
//            }
        }
    }

    private void splitMap(Map<Integer, List<EveOrder>> leftMap, Map<Integer, List<EveOrder>> rightMap) {
        Set<Integer> objects = orderMap.keySet();
        int size = objects.size();
        List<Integer> subLeft = CollUtil.sub(objects, 0, size / 2);
        List<Integer> subRight = CollUtil.sub(objects, size / 2, size);
        Integer[] leftKey = new Integer[size / 2];
        Integer[] rightKey = new Integer[size - size / 2];
        subLeft.toArray(leftKey);
        subRight.toArray(rightKey);
        leftMap.putAll(MapUtil.getAny(orderMap, leftKey));
        rightMap.putAll(MapUtil.getAny(orderMap, rightKey));
    }

    private void inputRfOrder(Map<Integer, OrderParseResult> result, Map<Integer, List<EveOrder>> rfOrder) {
        Iterator<Integer> iter = rfOrder.keySet().iterator();
        while (iter.hasNext()) {
            Integer typeID = iter.next();
            List<EveOrder> eveOrders = rfOrder.get(typeID);
            for(EveOrder order : eveOrders) {
                if(order.isBuyOrder()) {
                    continue;
                }
                OrderParseResult orderParseResult = result.get(typeID);
                if(orderParseResult == null) {
                    orderParseResult = new OrderParseResult();
                }
                orderParseResult.setTypeID(typeID);

                Double orderPrice = Double.valueOf(order.getPrice());
                if(orderPrice < orderParseResult.getMinPrice()) {
                    orderParseResult.setMinPrice(orderPrice);
                }
                if(orderPrice > orderParseResult.getMaxPrice()) {
                    orderParseResult.setMaxPrice(orderPrice);
                }
                //TODO 平均值未设置，是否添加有待考察
                orderParseResult.setVolRemain(orderParseResult.getVolRemain() + order.getVolumeRemain());
                result.put(typeID, orderParseResult);
            }
        }
    }

    private void queryJitaOrderInfoFromEsi(Map<Integer, OrderParseResult> map) {
        Iterator<Integer> iter = map.keySet().iterator();
        while (iter.hasNext()) {
            Integer id = iter.next();
            OrderParseResult orderParseResult = map.get(id);
            orderParseResult.setEveMarketData(getJitaOrderData(id));
        }
    }

    private EveMarketData getJitaOrderData(Integer id) {
        List<EveOrder> regionSellOrder = TradeUtil.getRegionOrder(id, PrjConst.REGION_ID_THE_FORGE);
        double min = Integer.MAX_VALUE;
        double max = -1;
        for(EveOrder order : regionSellOrder) {
            if(!PrjConst.STATION_ID_JITA_NAVY4.equals(order.getLocationId())) {
                continue;
            }
            if(order.isBuyOrder()) {
                double orderPrice = order.getPrice();
                if(orderPrice > max) {
                    max = orderPrice;
                }
            } else {
                double orderPrice = order.getPrice();
                if(orderPrice < min) {
                    min = orderPrice;
                }
            }
        }
        EveMarketData data = new EveMarketData();
        EveMarketForQuery forQuery = new EveMarketForQuery();
        List<String> types = new ArrayList<>();
        types.add(String.valueOf(id));
        forQuery.setTypes(types);

        EveMarketSellOrder eveMarketSellOrder = new EveMarketSellOrder();
        eveMarketSellOrder.setForQuery(forQuery);
        eveMarketSellOrder.setMin(min);

        EveMarketBuyOrder eveMarketBuyOrder = new EveMarketBuyOrder();
        eveMarketBuyOrder.setForQuery(forQuery);
        eveMarketBuyOrder.setMax(max);

        data.setSell(eveMarketSellOrder);
        data.setBuy(eveMarketBuyOrder);
        return data;
    }

    private void queryJitaOrderInfoTT(Map<Integer, OrderParseResult> orderResult) {
        List<Integer> typeIDs = new ArrayList<>(orderResult.keySet());
        for (int i = 0; i < typeIDs.size(); i = i + 200) {
            int end = NumberUtil.min(i + 200, typeIDs.size());
            List<Integer> sub = typeIDs.subList(i, end);

            HashMap<String, Object> paramMap = new HashMap<>();
            paramMap.put("typeid", listToString(sub));
            paramMap.put("usesystem", PrjConst.SOLAR_SYSTEM_ID_JITA);
            String result= HttpUtil.get("https://api.evemarketer" +
                    ".com/ec/marketstat/json", paramMap);
            List<EveMarketData> array = JSON.parseArray(result, EveMarketData.class);
            for(EveMarketData data : array) {
                OrderParseResult orderParseResult = orderResult.get(Integer.parseInt(data.getBuy().getForQuery().getTypes().get(0)));
                orderParseResult.setEveMarketData(data);
            }
        }
    }

    private static String listToString(List<Integer> list) {
        StringBuilder sb = new StringBuilder();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (i < list.size() - 1) {
                    sb.append(list.get(i) + ",");
                } else {
                    sb.append(list.get(i));
                }
            }
        }
        return sb.toString();
    }

    private void importInventory(Map<Integer, OrderParseResult> result, Map<Integer, Integer> selfOrderMap, Map<Integer, Integer> jitaInventory, Map<Integer, Integer> rfInventory) {
        Iterator<Integer> iter = result.keySet().iterator();
        while(iter.hasNext()) {
            Integer id = iter.next();
            OrderParseResult orderParseResult = result.get(id);
            Integer orderCnt = selfOrderMap.get(id);
            if(orderCnt != null) {
                orderParseResult.minusVolRemain(orderCnt);
                orderParseResult.addInventory(orderCnt);
            }
            Integer jitaCnt = jitaInventory.get(id);
            if(jitaCnt != null) {
                orderParseResult.addInventory(jitaCnt);
            }
            Integer rfCnt = rfInventory.get(id);
            if(rfCnt != null) {
                orderParseResult.addInventory(rfCnt);
            }
        }
    }

    private void getRecommendedPurchaseQuantity(Map<Integer, OrderParseResult> result) throws IOException {
        Iterator<Integer> iter = result.keySet().iterator();
        while (iter.hasNext()) {
            Integer typeID = iter.next();
            System.out.println("线程:" + Thread.currentThread().getName() + " 正在初步过滤id:" + typeID);
            OrderParseResult orderParseResult = result.get(typeID);
            double sellMin = orderParseResult.getEveMarketData().getSell().getMin();
            double buyMax = orderParseResult.getEveMarketData().getBuy().getMax();
            double rfSell = orderParseResult.getMinPrice();

            Items items = itemMap.get(typeID);
            if(items == null) {
                iter.remove();
                continue;
            }

            double expressFax = items.getVolume() * PrjConst.EXPRESS_FAX_CUBIC_METRES_RF_TO_JITA + PrjConst.EXPRESS_FAX_RF_TO_JITA_EXTRA * buyMax;

//            double profitBySellOrder = sellMin - rfSell - expressFax;
            double profitByBuyOrder = buyMax * (1 - PrjConst.SELL_FAX) - rfSell - expressFax;
//
//            if(profitBySellOrder < 0 && profitByBuyOrder < 0) {
//                iter.remove();
//                continue;
//            }
            if(profitByBuyOrder < 0) {
                iter.remove();
                continue;
            }
            orderParseResult.setItem(items);
            orderParseResult.setProfitByBuyOrder(profitByBuyOrder);
        }
    }
}
