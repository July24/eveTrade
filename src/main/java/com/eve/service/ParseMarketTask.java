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
    int hopeProfit;
    double hopeMargin;

    public ParseMarketTask(Map<Integer, Integer> selfOrderMap, Map<Integer, Integer> jitaInventory, Map<Integer, Integer> rfInventory, Map<Integer, List<EveOrder>> orderMap, Map<Integer, Items> itemMap, int hopeProfit, double hopeMargin) {
        this.selfOrderMap = selfOrderMap;
        this.jitaInventory = jitaInventory;
        this.rfInventory = rfInventory;
        this.orderMap = orderMap;
        this.itemMap = itemMap;
        this.hopeProfit = hopeProfit;
        this.hopeMargin = hopeMargin;
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
                getRecommendedPurchaseQuantity(result, hopeProfit, hopeMargin, itemMap);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        } else {
            HashMap<Integer, List<EveOrder>> leftMap = new HashMap<>();
            HashMap<Integer, List<EveOrder>> rightMap = new HashMap<>();
            splitMap(leftMap, rightMap);
            ParseMarketTask left = new ParseMarketTask(selfOrderMap, jitaInventory, rfInventory, leftMap, itemMap,
                    hopeProfit, hopeMargin);
            ParseMarketTask right = new ParseMarketTask(selfOrderMap, jitaInventory, rfInventory, rightMap, itemMap,
                    hopeProfit, hopeMargin);
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

    public static void main(String[] args) {
        ParseMarketTask task = new ParseMarketTask(null, null, null, null, null,0,0);
        task.getRegionOrder(609);

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
                if(order.getPrice() < orderParseResult.getMinPrice()) {
                    orderParseResult.setMinPrice(order.getPrice());
                }
                if(order.getPrice() > orderParseResult.getMaxPrice()) {
                    orderParseResult.setMaxPrice(order.getPrice());
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
        List<EveOrder> regionSellOrder = getRegionOrder(id);
        double min = Integer.MAX_VALUE;
        for(EveOrder order : regionSellOrder) {
            if(!PrjConst.STATION_ID_JITA_NAVY4.equals(order.getLocationId())) {
                continue;
            }
            if(order.isBuyOrder()) {
                continue;
            }
            if(order.getPrice() < min) {
                min = order.getPrice();
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

    private List<EveOrder> getRegionOrder(Integer id) {
        List<EveOrder> ret = new ArrayList<>();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("datasource", PrjConst.DATASOURCE);
        paramMap.put("page", 1);
        paramMap.put("type_id", id);
        int cnt = 0;
        while (cnt < 3) {
            HttpResponse httpResponse = sendGetRequest(replaceBraces(PrjConst.ORDER_URL,
                    PrjConst.REGION_ID_THE_FORGE), paramMap);
            int status = httpResponse.getStatus();
            if(status != 200) {
                cnt++;
                continue;
            }

            String body = httpResponse.body();
            ret.addAll(JSON.parseArray(body, EveOrder.class));
            long maxPage = Long.parseLong(httpResponse.header("x-pages"));
            for (int i = 2; i <= maxPage; i ++) {
                Map<String, Object> paramMap2 = new HashMap<>();
                paramMap2.put("datasource", PrjConst.DATASOURCE);
                paramMap2.put("page", i);
                paramMap2.put("type_id", id);
                int cnt2 = 0;
                while (cnt2 < 3) {
                    HttpResponse httpResponse2 = sendGetRequest(replaceBraces(PrjConst.ORDER_URL,
                            PrjConst.REGION_ID_THE_FORGE), paramMap2);
                    int status2 = httpResponse2.getStatus();
                    if (status2 != 200) {
                        cnt2++;
                        continue;
                    }
                    ret.addAll(JSON.parseArray(httpResponse2.body(), EveOrder.class));
                    break;
                }
            }
            break;
        }
        return ret;
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
            if(orderCnt != null) {
                orderParseResult.addInventory(jitaCnt);
            }
            Integer rfCnt = rfInventory.get(id);
            if(orderCnt != null) {
                orderParseResult.addInventory(rfCnt);
            }
        }
    }

    private void getRecommendedPurchaseQuantity(Map<Integer, OrderParseResult> result, int hopeProfit,
                                                double hopeMargin, Map<Integer, Items> itemMap) throws IOException {
        filterItemAndGetDailyVolumn(result, hopeProfit, hopeMargin);
        computeFilterProfit(result, hopeProfit, hopeMargin, itemMap);
    }

    private void computeFilterProfit(Map<Integer, OrderParseResult> result, int hopeProfit,
                                     double hopeMargin, Map<Integer, Items> itemMap) {
        Iterator<Integer> iter = result.keySet().iterator();
        while(iter.hasNext()) {
            Integer id = iter.next();

            System.out.println("线程:" + Thread.currentThread().getName() + "id:" + id + " 正在计算利润和购买量");

            OrderParseResult parseResult = result.get(id);
            Items items = itemMap.get(id);
            if(items == null) {
                continue;
            }
            parseResult.newComputerProfit(items.getVolumn());
//            parseResult.computerProfit();
            //TODO 只拿到了自己当前的垄断，无法获得有购买记录而无卖单的垄断单
            if(parseResult.isMonopoly()) {
                parseResult.predictMonoPurchaseCount();
                if(parseResult.getRecommendedCount() < 1) {
                    iter.remove();
                }
            } else {
                if(parseResult.getStatisticData().getProfit() < hopeProfit && parseResult.getStatisticData().getProfitMargin() < hopeMargin) {
                    iter.remove();
                    continue;
                }
                parseResult.predictPurchaseCount();
                if (parseResult.getRecommendedCount() <= 0) {
                    iter.remove();
                }
            }
        }
    }

    private void filterItemAndGetDailyVolumn(Map<Integer, OrderParseResult> result, int hopeProfit, double hopeMargin) {
        Iterator<Integer> iter = result.keySet().iterator();
        while (iter.hasNext()) {
            Integer typeID = iter.next();
            System.out.println("线程:" + Thread.currentThread().getName() + " 正在初步过滤id:" + typeID);
            OrderParseResult orderParseResult = result.get(typeID);
            String jitaSellMin = orderParseResult.getEveMarketData().getSell().getMin();
            double min = jitaSellMin == null ? Long.MAX_VALUE :
                    Double.parseDouble(orderParseResult.getEveMarketData().getSell().getMin());
            double orderMin = orderParseResult.getMinPrice();
            double profit = orderMin - min;
            double margin = profit/min;
            if(profit < hopeProfit && margin < hopeMargin) {
                iter.remove();
                continue;
            }
            double dailyVolume = getDailyVolume(typeID);
            if(dailyVolume < PrjConst.FILTER_LOW_FLOW) {
                iter.remove();
                continue;
            }
            orderParseResult.setDailySalesVolume(dailyVolume);
        }
    }

    private double getDailyVolume(int typeID) {
        int getCnt = 0;
        while(getCnt < 3) {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("datasource", PrjConst.DATASOURCE);
            paramMap.put("type_id", typeID);
            HttpResponse httpResponse =
                    sendGetRequest(replaceBraces(PrjConst.ORDER_HISTORY_URL,
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

    private HttpResponse sendGetRequest(String url, Map<String, Object> paramMap) {
        return HttpRequest.get(url).form(paramMap).execute();
    }

    private String replaceBraces(String url, String replace) {
        StringBuilder sb = new StringBuilder(url);
        int i1 = sb.indexOf("{");
        int i2 = sb.indexOf("}") + 1;
        StringBuilder newSb = sb.replace(i1, i2, replace);
        return newSb.toString();
    }
}
