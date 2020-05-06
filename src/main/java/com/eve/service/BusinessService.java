package com.eve.service;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.eve.dao.ItemsMapper;
import com.eve.entity.EveMarketData;
import com.eve.entity.EveOrder;
import com.eve.entity.OrderHistory;
import com.eve.entity.OrderParseResult;
import com.eve.entity.database.Items;
import com.eve.util.PrjConst;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.*;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

public class BusinessService {
    public static void main(String[] args) throws Exception {
        BusinessService bs = new BusinessService();
        bs.parseStationMarket(PrjConst.STATION_ID_RF_WINTERCO, null, 400000,
                0.4);
    }

    private String getAccessToken() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("grant_type", "refresh_token");
        paramMap.put("refresh_token", PrjConst.SANJI_REFRESH_TOKEN);
        String body = JSON.toJSONString(paramMap);
        String auth = "Basic "+Base64.encode(PrjConst.CLINET_ID+":"+PrjConst.SECRET_KEY);
        String ret = HttpRequest.post(PrjConst.TOKEN_URL)
                .header(Header.CONTENT_TYPE, "application/json")//头信息，多个头信息多次调用此方法即可
                .header(Header.AUTHORIZATION, auth)
                .body(body)//表单内容
                .execute().body();
        Map map = JSON.parseObject(ret, Map.class);
        return (String) map.get("access_token");
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

    private HashMap<Integer, List<EveOrder>> getRfOrder(String accessToken, String stationID) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("datasource", PrjConst.DATASOURCE);
        paramMap.put("page", 1);
        paramMap.put("token", accessToken);
        HttpResponse httpResponse = sendGetRequest(replaceBraces(PrjConst.LIST_STRUCTURE_ORDER_URL,
                stationID), paramMap);
        String body = httpResponse.body();
        List<EveOrder> eveOrders = JSON.parseArray(body, EveOrder.class);
        int size = Integer.parseInt(httpResponse.header("x-pages"));
        for(int i = 2; i <= size; i++) {
            Map<String, Object> paramMap2 = new HashMap<>();
            paramMap2.put("datasource", PrjConst.DATASOURCE);
            paramMap2.put("page", i);
            paramMap2.put("token", accessToken);
            String body2 =
                    sendGetRequest(replaceBraces(PrjConst.LIST_STRUCTURE_ORDER_URL,
                    stationID), paramMap2).body();
            if(body2.contains("error")) {
                i--;
                continue;
            }
            List<EveOrder> eveOrders2 = JSON.parseArray(body2, EveOrder.class);
            eveOrders.addAll(eveOrders2);
        }
        return settleStationOrder(eveOrders);
    }

    private HashMap<Integer, List<EveOrder>> settleStationOrder(List<EveOrder> eveOrders) {
        HashMap<Integer, List<EveOrder>> map = new HashMap<>();
        for(EveOrder eveOrder : eveOrders) {
            List<EveOrder> typeOrders = map.get(eveOrder.getTypeId());
            if(typeOrders == null) {
                typeOrders = new ArrayList<>();
            }
            typeOrders.add(eveOrder);
            map.put(eveOrder.getTypeId(), typeOrders);
        }
        return map;
    }

    public void parseStationMarket(String stationID, String orderListPath,
                                   int hopeProfit, double hopeMargin) throws Exception {
        String accessToken = getAccessToken();
        Map<Integer, List<EveOrder>> orderMap = getRfOrder(accessToken, stationID);
        orderMap = pageOrderMap(orderMap, 0,5);
        HashMap<Integer, Items> itemMap = getItemMap();
        HashMap<Integer, Integer> selfOrderMap = getOrderMap(orderListPath);
        HashMap<String, Integer> enNameMap = getEnNameMap(itemMap);
        HashMap<Integer, Integer> jitaInventory = getWarehouseMap(PrjConst.PATH_JITA_INVENTORY, enNameMap);
        HashMap<Integer, Integer> rfInventory = getWarehouseMap(PrjConst.PATH_RF_INVENTORY, enNameMap);

        ForkJoinPool pool = new ForkJoinPool();
        ParseMarketTask task = new ParseMarketTask(selfOrderMap, jitaInventory, rfInventory, orderMap, itemMap,
                hopeProfit, hopeMargin);
        pool.invoke(task);
        Map<Integer, OrderParseResult> result = task.join();
        outRecommendedSimple(result, itemMap);

        //TODO map reduce
        //TODO 分次进行，同时解析Jita订单时注意返回错误格式的处理办法
//        rfOrder = rfOrder.subList(100, 200);
//        Map<Integer, OrderParseResult> result = new HashMap<>();
//        inputRfOrder(result, orderMap);
//        System.out.println("导入RF订单完毕");
//        queryJitaOrderInfoTT(result);
//        System.out.println("导入Jita订单完毕");
//        System.out.println("getItemMap完毕");
//        importInventory(result, selfOrderMap, jitaInventory, rfInventory);//todo 传入订单/库存
//        System.out.println("importInventory完毕");
//        getRecommendedPurchaseQuantity(result, hopeProfit, profitMargin, itemMap);
        //TODO map reduce
        //TODO 输出
    }

    private Map<Integer, List<EveOrder>> pageOrderMap(Map<Integer, List<EveOrder>> orderMap, int from, int to) throws Exception {
        int size = orderMap.size();
        System.out.println("一共有品类:" +size);
        if(to > size) {
            throw new Exception("输出范围错误");
        }
        Set<Integer> keySet = orderMap.keySet();
        List<Integer> sub = CollUtil.sub(keySet, from, to);
        Integer[] subKey = new Integer[to - from];
        sub.toArray(subKey);
        return MapUtil.getAny(orderMap, subKey);
    }

    private void importInventory(Map<Integer, OrderParseResult> result, HashMap<Integer, Integer> selfOrderMap, HashMap<Integer, Integer> jitaInventory, HashMap<Integer, Integer> rfInventory) {
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

    private HashMap<Integer, Items> getItemMap() throws IOException {
        ItemsMapper itemsMapper = getItemsMapper();
        List<Items> items = itemsMapper.selectByExample(null);
        HashMap<Integer, Items> map = new HashMap<>();
        for(Items item : items) {
            map.put(item.getId(), item);
        }
        return map;
    }

    private void getRecommendedPurchaseQuantity(Map<Integer, OrderParseResult> result, int hopeProfit,
                                                double hopeMargin, HashMap<Integer, Items> itemMap) throws IOException {
        filterItemAndGetDailyVolumn(result, hopeProfit, hopeMargin);
        computeFilterProfit(result, hopeProfit, hopeMargin, itemMap);
    }

    private void outRecommendedSimple(Map<Integer, OrderParseResult> result, HashMap<Integer, Items> itemMap) throws IOException {
        File file = new File("result/trade/simple");
        List<OrderParseResult> monopoly = new ArrayList<>();
        FileWriter writer = new FileWriter(file);
        Iterator<Integer> iter = result.keySet().iterator();
        while (iter.hasNext()) {
            Integer id = iter.next();
            OrderParseResult orderParseResult = result.get(id);
            if(orderParseResult.isMonopoly()) {
                monopoly.add(orderParseResult);
                continue;
            }
            String record = getPurchaseRecord(orderParseResult, itemMap.get(id).getEnName());
            writer.write(record);
            writer.write("\r\n");
        }
        for (OrderParseResult mono : monopoly) {
            writer.write("-----------------------------------");
            writer.write("\r\n");
            writer.write("--------------monopoly-------------");
            writer.write("\r\n");
            writer.write("-----------------------------------");
            writer.write("\r\n");
            writer.write(getPurchaseRecord(mono, itemMap.get(mono.getTypeID()).getEnName()));
            writer.write("\r\n");
        }
        writer.flush();
        writer.close();
    }

    private String getPurchaseRecord(OrderParseResult orderParseResult, String enName) {
        StringBuilder sb = new StringBuilder();
        sb.append(enName);
        sb.append(" x");
        sb.append(orderParseResult.getRecommendedCount());
        return sb.toString();
    }

    private void getItemInfo(Map<Integer, OrderParseResult> result, HashMap<Integer, Items> itemMap) {
        Iterator<Integer> iter = result.keySet().iterator();
        while (iter.hasNext()) {
            Integer typeID = iter.next();
            OrderParseResult orderParseResult = result.get(typeID);
            orderParseResult.setItem(itemMap.get(typeID));
        }
    }

    private void filterItemAndGetDailyVolumn(Map<Integer, OrderParseResult> result, int hopeProfit, double hopeMargin) {
        Iterator<Integer> iter = result.keySet().iterator();
        while (iter.hasNext()) {
            Integer typeID = iter.next();
            System.out.println("正在初步过滤id:" + typeID);
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
            orderParseResult.setDailySalesVolume(getDailyVolumn(typeID));
        }
    }

    private double getDailyVolumn(int typeID) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("datasource", PrjConst.DATASOURCE);
        paramMap.put("type_id", typeID);
        HttpResponse httpResponse =
                sendGetRequest(replaceBraces(PrjConst.ORDER_HISTORY_URL,
                        PrjConst.REGION_ID_OASA), paramMap);
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

    private void computeFilterProfit(Map<Integer, OrderParseResult> result, int hopeProfit,
                                     double hopeMargin, HashMap<Integer, Items> itemMap) {
        Iterator<Integer> iter = result.keySet().iterator();
        while(iter.hasNext()) {
            Integer id = iter.next();

            System.out.println("id:" + id + " 正在计算利润和购买量");

            OrderParseResult parseResult = result.get(id);
            parseResult.newComputerProfit(itemMap.get(id).getVolumn());
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

    private void importInventory(Map<Integer, OrderParseResult> result, String orderListPath,
                                 HashMap<Integer, Items> itemsMapper) throws IOException {
        if(orderListPath != null) {
            importOrderList(result, orderListPath);
        }
        HashMap<String, Integer> enNameMap = getEnNameMap(itemsMapper);
        importWarehouse(result, PrjConst.PATH_JITA_INVENTORY, enNameMap);
        importWarehouse(result, PrjConst.PATH_RF_INVENTORY, enNameMap);
    }

    private HashMap<String, Integer> getEnNameMap(HashMap<Integer, Items> itemsMapper) {
        HashMap<String, Integer> enNameMap = new HashMap<>();
        Iterator<Integer> iter = itemsMapper.keySet().iterator();
        while(iter.hasNext()) {
            Integer id = iter.next();
            Items items = itemsMapper.get(id);
            enNameMap.put(items.getEnName(), id);
        }
        return enNameMap;
    }

    private HashMap<Integer, Integer> getOrderMap(String orderListPath) throws IOException {
        HashMap<Integer, Integer> orderMap = new HashMap<>();
        if(orderListPath == null) {
            return orderMap;
        }
        Reader in = new FileReader(new File(orderListPath));
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
        for (CSVRecord record : records) {
            if("True".equalsIgnoreCase(record.get("bid"))) {
                continue;
            }
            int volRemaining = Double.valueOf(record.get("volRemaining")).intValue();
            orderMap.put(Integer.parseInt(record.get("typeID")), volRemaining);
        }
        in.close();
        return orderMap;
    }

    private HashMap<Integer, Integer> getWarehouseMap(String wareHousePath, HashMap<String, Integer> enNameMap) {
        HashMap<Integer, Integer> wareMap = new HashMap<>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File(wareHousePath)));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                String[] split = tempString.split("\t");
                String en_name = split[0];
                Integer id = enNameMap.get(en_name);
                if(id == null) {
                    continue;
                }
                int remain = Integer.parseInt(split[1].replace(",", ""));
                wareMap.put(id, remain);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return wareMap;
    }

    private void importOrderList(Map<Integer, OrderParseResult> result, String orderListPath) throws IOException {
        Reader in = new FileReader(new File(orderListPath));
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
        for (CSVRecord record : records) {
            if("True".equalsIgnoreCase(record.get("bid"))) {
                continue;
            }
            OrderParseResult parse = result.get(Integer.parseInt(record.get("typeID")));
            if(parse == null) {
                continue;
            }
            int volRemaining = Double.valueOf(record.get("volRemaining")).intValue();
            parse.addInventory(volRemaining);
            parse.minusVolRemain(volRemaining);
        }
    }

    private void importWarehouse(Map<Integer, OrderParseResult> result, String rfWareHousePath, HashMap<String, Integer> enNameMap) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File(rfWareHousePath)));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                String[] split = tempString.split("\t");
                String en_name = split[0];
                Integer id = enNameMap.get(en_name);
                if(id == null) {
                    continue;
                }
                OrderParseResult orderParseResult = result.get(id);
                if(orderParseResult == null) {
                    continue;
                }
                int remain = Integer.parseInt(split[1].replace(",", ""));
                orderParseResult.addInventory(remain);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    private ItemsMapper getItemsMapper() throws IOException {
        String mybatisConfigPath = "mybatisconfig.xml";
        InputStream inputStream = Resources.getResourceAsStream(mybatisConfigPath);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        return sqlSession.getMapper(ItemsMapper.class);
    }

    private void setJitaItemInfo(Map<Integer, OrderParseResult> result) throws IOException {
        Set<Integer> set = result.keySet();
        Map<Integer, EveMarketData> map = queryJitaOrderInfo(new ArrayList<>(set));
        Iterator<Integer> iter = set.iterator();
        while(iter.hasNext()) {
            Integer id = iter.next();
            result.get(id).setEveMarketData(map.get(id));
        }
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

    private Map<Integer, EveMarketData> queryJitaOrderInfo(List<Integer> typeIDs) {
        Map<Integer, EveMarketData> ret = new HashMap<>();
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
                ret.put(Integer.parseInt(data.getBuy().getForQuery().getTypes().get(0)), data);
            }
        }
        return ret;
    }

    private String listToString(List<Integer> list) {
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

    private void inputRfOrder(Map<Integer, OrderParseResult> result, HashMap<Integer, List<EveOrder>> rfOrder) {
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
}
