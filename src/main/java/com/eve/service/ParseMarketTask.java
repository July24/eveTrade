package com.eve.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.http.HttpRequest;
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

public class ParseMarketTask extends RecursiveTask<Map<Integer, OrderParseResult>> {
    int THRESHOLD = 30;
    Map<Integer, Integer> selfOrderMap;
    Map<Integer, Integer> jitaInventory;
    Map<Integer, Integer> rfInventory;
    Map<Integer, List<EveOrder>> orderMap;
    Map<Integer, Items> itemMap;
    double hopeRoi;
    int flowFilter;

    public ParseMarketTask(Map<Integer, Integer> selfOrderMap, Map<Integer, Integer> jitaInventory, Map<Integer,
            Integer> rfInventory, Map<Integer, List<EveOrder>> orderMap, Map<Integer, Items> itemMap,
                           int flowFilter, double hopeRoi) {
        this.selfOrderMap = selfOrderMap;
        this.jitaInventory = jitaInventory;
        this.rfInventory = rfInventory;
        this.orderMap = orderMap;
        this.itemMap = itemMap;
        this.flowFilter = flowFilter;
        this.hopeRoi = hopeRoi;
    }

    @Override
    protected Map<Integer, OrderParseResult> compute() {
        if (orderMap.size() <= THRESHOLD) {
            // 直接求和
            Map<Integer, OrderParseResult> result = new HashMap<>();
            inputRfOrder(result, orderMap);
//            System.out.println("导入RF订单完毕");
//            queryJitaOrderInfoTT(result);
            // TODO esi中获取数据
            queryJitaOrderInfoFromEsi(result);
//            System.out.println("导入Jita订单完毕");
//            System.out.println("getItemMap完毕");
            importInventory(result, selfOrderMap, jitaInventory, rfInventory);//todo 传入订单/库存
//            System.out.println("importInventory完毕");
            try {
                getRecommendedPurchaseQuantity(result, flowFilter, hopeRoi, itemMap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        } else {
            HashMap<Integer, List<EveOrder>> leftMap = new HashMap<>();
            HashMap<Integer, List<EveOrder>> rightMap = new HashMap<>();
            splitMap(leftMap, rightMap);
            ParseMarketTask left = new ParseMarketTask(selfOrderMap, jitaInventory, rfInventory, leftMap, itemMap,
                    flowFilter, hopeRoi);
            ParseMarketTask right = new ParseMarketTask(selfOrderMap, jitaInventory, rfInventory, rightMap, itemMap,
                    flowFilter, hopeRoi);
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
            orderParseResult.setEveMarketData(getJitaSellOrder(id));
        }
    }

    private EveMarketData getJitaSellOrder(Integer id) {
        List<EveOrder> regionSellOrder = TradeUtil.getRegionOrder(id, PrjConst.REGION_ID_THE_FORGE);
        double min = Integer.MAX_VALUE;
        for(EveOrder order : regionSellOrder) {
            if(!PrjConst.STATION_ID_JITA_NAVY4.equals(order.getLocationId())) {
                continue;
            }
            if(order.isBuyOrder()) {
                continue;
            }
            Double orderPrice = Double.valueOf(order.getPrice());
            if(orderPrice < min) {
                min = orderPrice;
            }
        }
        EveMarketData data = new EveMarketData();
        EveMarketSellOrder eveMarketSellOrder = new EveMarketSellOrder();
        EveMarketForQuery forQuery = new EveMarketForQuery();
        List<String> types = new ArrayList<>();
        types.add(String.valueOf(id));
        forQuery.setTypes(types);
        eveMarketSellOrder.setForQuery(forQuery);
        eveMarketSellOrder.setMin(String.valueOf(min));
        data.setSell(eveMarketSellOrder);
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

    private void getRecommendedPurchaseQuantity(Map<Integer, OrderParseResult> result,
                                                int flowFilter, double hopeRoi, Map<Integer, Items> itemMap) throws IOException {
        Iterator<Integer> iter = result.keySet().iterator();
        while (iter.hasNext()) {
            Integer typeID = iter.next();
            if(typeID == 33844) {
                System.out.println(typeID);
            }
            System.out.println("线程:" + Thread.currentThread().getName() + " 正在初步过滤id:" + typeID);
            OrderParseResult orderParseResult = result.get(typeID);
            String jitaSellMin = orderParseResult.getEveMarketData().getSell().getMin();
            double min = jitaSellMin == null ? Long.MAX_VALUE :
                    Double.parseDouble(orderParseResult.getEveMarketData().getSell().getMin());
            double orderMin = orderParseResult.getMinPrice();
            double profit = orderMin - min;
            if(profit < 0) {
                iter.remove();
                continue;
            }
            double dailyVolume = getDailyVolume(typeID);
            if(dailyVolume < flowFilter) {
                iter.remove();
                continue;
            }
            orderParseResult.setDailySalesVolume(dailyVolume);

            Items items = itemMap.get(typeID);
            if(items == null) {
                iter.remove();
                continue;
            }
            orderParseResult.setItem(items);
            orderParseResult.computerProfit();
            if(orderParseResult.getStatisticData().getProfitMargin() < hopeRoi) {
                iter.remove();
                continue;
            }
            orderParseResult.predictPurchaseCount();
            if (orderParseResult.getRecommendedCount() <= 0) {
                iter.remove();
                continue;
            }
            orderParseResult.computeTotalProfit();
        }
    }

    private double getDailyVolume(int typeID) {
        int getCnt = 0;
        while(getCnt < 3) {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("datasource", PrjConst.DATASOURCE);
            paramMap.put("type_id", typeID);
            HttpResponse httpResponse =
                    TradeUtil.sendGetRequest(TradeUtil.replaceBraces(PrjConst.ORDER_HISTORY_URL,
                            PrjConst.REGION_ID_OASA), paramMap);
            int status = httpResponse.getStatus();
            if(status != 200) {
                getCnt++;
                continue;
            }
            List<OrderHistory> orderHistories = JSON.parseArray(httpResponse.body(), OrderHistory.class);
            int size = orderHistories.size();
            Date endDate = new Date();
            DateTime bgnDate = DateUtil.offsetDay(endDate, -8);
            double sum = 0;
            for(int i = size - 1; i >= 0; i--) {
                OrderHistory orderHistory = orderHistories.get(i);
                Date date = orderHistory.getDate();
                if(date.before(endDate) && date.after(bgnDate)) {
                    sum += orderHistory.getVolume();
                }
            }
            return sum/7;
        }
        return 0;
    }


}
