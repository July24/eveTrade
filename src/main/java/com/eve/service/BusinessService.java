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
import com.eve.dao.IndustryActivityProductsMapper;
import com.eve.dao.ItemsMapper;
import com.eve.dao.MarketgroupsMapper;
import com.eve.entity.*;
import com.eve.entity.database.*;
import com.eve.util.DBConst;
import com.eve.util.PrjConst;
import com.eve.util.TradeUtil;
import io.swagger.models.auth.In;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

public class BusinessService extends ServiceBase {
    public static void main(String[] args) throws Exception {
        BusinessService bs = new BusinessService();
        AuthAccount account = new AuthAccount(PrjConst.ALLEN_CHAR_ID, PrjConst.ALLEN_CHAR_NAME, PrjConst.ALLEN_REFRESH_TOKEN);
        AuthAccount jitaAccount = new AuthAccount(PrjConst.LEAH_CHAR_ID, PrjConst.LEAH_CHAR_NAME,
                PrjConst.LEAH_REFRESH_TOKEN);
        bs.parseStationMarket(account, jitaAccount, PrjConst.STATION_ID_RF_WINTERCO,
                3, 0.2, true);
//        bs.getChangeItemList(account);
//        bs.getJitaBuyChangeItemList(jitaAccount);

//        bs.parseStationExportMarket(account, PrjConst.STATION_ID_RF_WINTERCO);

        List<Integer> exclude = new ArrayList<>();
        exclude.add(601);
        exclude.add(648);
        exclude.add(649);
        exclude.add(16242);
//        bs.getRfRelistItem(account, exclude);

    }

    public void getRfRelistItem(AuthAccount account, List<Integer> exclude) throws Exception {
        List<AssertItem> anAssert = getAssert(account);
        List<AssertItem> rfAssert = filterRFAssert(anAssert, exclude,
                PrjConst.STATION_ID_RF_WINTERCO);
        Set<Integer> orderIDs = getMyOrder(account).keySet();
        List<Items> notList = getNotList(rfAssert, orderIDs);
        outRelist(notList);
    }

    private void outRelist(List<Items> items) throws IOException {
        File file = new File("result/trade/InventoryRelist");
        DecimalFormat df = new DecimalFormat( "#,###.00");
        FileWriter writer = new FileWriter(file);
        Iterator<Items> iter = items.iterator();
        while (iter.hasNext()) {
            Items item = iter.next();
            writer.write(item.getEnName());
            writer.write("\t");
            String format = df.format(getJitaSellPrice(item.getId()));
            writer.write(format);
            writer.write("\r\n");
        }
        writer.flush();
        writer.close();
    }

    private List<Items> getNotList(List<AssertItem> rfAssert, Set<Integer> orderIDs) throws Exception {
        List<Integer> ids = new ArrayList<>();
        for(AssertItem item : rfAssert) {
            if(!orderIDs.contains(item.getTypeId())) {
                ids.add(item.getTypeId());
            }
        }
        if(ids.size() == 0) {
            return new ArrayList<>();
        }
        ItemsMapper itemsMapper = getItemsMapper();
        ItemsExample example = new ItemsExample();
        example.createCriteria().andIdIn(ids);
        return itemsMapper.selectByExample(example);
    }

    private List<AssertItem> filterRFAssert(List<AssertItem> anAssert, List<Integer> exclude, String locationID) {
        List<AssertItem> ret = new ArrayList<>();
        for(AssertItem item : anAssert) {
            if(Long.parseLong(locationID) == item.getLocationId() && !exclude.contains(item.getTypeId())) {
                ret.add(item);
            }
        }
        return ret;
    }

    public List<AssertItem> getAssert(AuthAccount account) throws Exception {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("datasource", PrjConst.DATASOURCE);
        paramMap.put("page", 1);
        paramMap.put("token", account.getAccessToken());
        HttpResponse httpResponse = sendGetRequest(replaceBraces(PrjConst.CHAR_ASSERT_URL,
                account.getId()), paramMap);
        String body = httpResponse.body();
        return JSON.parseArray(body, AssertItem.class);
    }

    public void getChangeItemList(AuthAccount account) throws Exception {
        Map<Integer, List<EveOrder>> myOrder = getMyOrder(account);
        Map<Integer, List<EveOrder>> orderMap = getRfOrder(account.getAccessToken(), PrjConst.STATION_ID_RF_WINTERCO);
        List<Items> changeList = getChangeList(myOrder, orderMap);
        outChangeList(changeList);
    }

    public void getJitaBuyChangeItemList(AuthAccount account) throws Exception {
        Map<Integer, List<EveOrder>> myOrder = getMyOrder(account);
        List<Items> changeList = getJitaChangeList(myOrder);
        outJitaNotHighBuyList(changeList);
    }

    private void outJitaNotHighBuyList(List<Items> changeList) throws Exception {
        File file = new File("result/trade/jitaBuyNotHigh");
        FileWriter writer = new FileWriter(file);
        for(Items item : changeList) {
            writer.write(item.getEnName());
            writer.write("\r\n");
        }
        writer.flush();
        writer.close();
    }

    private List<Items> getJitaChangeList(Map<Integer, List<EveOrder>> myOrder) {
        List<Integer> idList = new ArrayList<>();
        Iterator<Integer> iter = myOrder.keySet().iterator();
        while(iter.hasNext()) {
            Integer id = iter.next();
            List<EveOrder> orderList = myOrder.get(id);
            double high = getHighestBuySell(orderList);
            if(high < 0) {
                continue;
            }
            double jitaHigh = getJitaBuyHighPrice(id);
            if(jitaHigh > high) {
                idList.add(id);
            }
        }
        ItemsMapper itemsMapper = getItemsMapper();
        ItemsExample example = new ItemsExample();
        example.createCriteria().andIdIn(idList);
        return itemsMapper.selectByExample(example);
    }

    private double getJitaBuyHighPrice(Integer id) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("datasource", PrjConst.DATASOURCE);
        paramMap.put("page", 1);
        paramMap.put("type_id", id);
        paramMap.put("order_type ", "buy");
        HttpResponse httpResponse = sendGetRequest(replaceBraces(PrjConst.LIST_REGION_ORDER_URL,
                PrjConst.REGION_ID_THE_FORGE), paramMap);
        String body = httpResponse.body();
        List<EveOrder> eveOrders = JSON.parseArray(body, EveOrder.class);
        int size = Integer.parseInt(httpResponse.header("x-pages"));
        for(int i = 2; i <= size; i++) {
            Map<String, Object> paramMap2 = new HashMap<>();
            paramMap2.put("datasource", PrjConst.DATASOURCE);
            paramMap2.put("page", i);
            paramMap2.put("type_id", id);
            paramMap2.put("order_type ", "buy");
            String body2 =
                    sendGetRequest(replaceBraces(PrjConst.LIST_REGION_ORDER_URL,
                            PrjConst.REGION_ID_THE_FORGE), paramMap2).body();
            if(body2.contains("error")) {
                i--;
                continue;
            }
            List<EveOrder> eveOrders2 = JSON.parseArray(body2, EveOrder.class);
            eveOrders.addAll(eveOrders2);
        }
        double buyHighest = 0;
        for(EveOrder order : eveOrders) {
            if(!order.isBuyOrder()) {
                continue;
            }
            if(!order.getLocationId().equals(PrjConst.STATION_ID_JITA_NAVY4)) {
                continue;
            }
            if(buyHighest < order.getPrice()) {
                buyHighest = order.getPrice();
            }
        }
        return buyHighest;
    }

    private double getHighestBuySell(List<EveOrder> orderList) {
        double high = -1;
        for(EveOrder order : orderList) {
            if(!order.isBuyOrder()) {
                continue;
            }
            if(high < order.getPrice()) {
                high = order.getPrice();
            }
        }
        return high;
    }

    private void outChangeList(List<Items> changeList) throws Exception {
        File file = new File("result/trade/changeList");
        FileWriter writer = new FileWriter(file);
        DecimalFormat df = new DecimalFormat( "#,###.00");
        for(Items item : changeList) {
            writer.write(item.getEnName());
            writer.write("\t");
            String format = df.format(getJitaSellPrice(item.getId()));
            writer.write(format);
            writer.write("\r\n");
        }
        writer.flush();
        writer.close();
    }

    private double getJitaSellPrice(Integer id) {
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
        return min;
    }

    private List<Items> getChangeList(Map<Integer, List<EveOrder>> myOrder, Map<Integer, List<EveOrder>> orderMap) throws Exception {
        List<Integer> changeList = new ArrayList<>();
        Map<Integer, Double> myLowestList = getMyTypeLowest(myOrder);
        Iterator<Integer> iter = myLowestList.keySet().iterator();
        while(iter.hasNext()) {
            Integer id = iter.next();
            Double myPrice = myLowestList.get(id);
            List<EveOrder> orderList = orderMap.get(id);
            for(EveOrder order : orderList) {
                if(order.isBuyOrder()) {
                    continue;
                }
                if(Double.valueOf(order.getPrice()) < myPrice) {
                    changeList.add(order.getTypeId());
                    break;
                }
            }
        }
        ItemsMapper itemsMapper = getItemsMapper();
        ItemsExample example = new ItemsExample();
        example.createCriteria().andIdIn(changeList);
        return itemsMapper.selectByExample(example);
    }

    private Map<Integer, Double> getMyTypeLowest(Map<Integer, List<EveOrder>> myOrder) {
        Map<Integer, Double> map = new HashMap<>();
        Iterator<Integer> iter = myOrder.keySet().iterator();
        while (iter.hasNext()) {
            Integer id = iter.next();
            List<EveOrder> orderList = myOrder.get(id);
            double lowest = (orderList.get(0).getPrice());
            for(int i = 1; i < orderList.size(); i++) {
                EveOrder order = orderList.get(i);
                if(order.isBuyOrder()) {
                    continue;
                }
                double price = ((order.getPrice()));
                if(price < lowest) {
                    lowest = price;
                }
            }
            map.put(id, lowest);
        }
        return map;
    }

    private Map<Integer, List<EveOrder>> getMyOrder(AuthAccount account) {
        Map<Integer, List<EveOrder>> ret = new HashMap<>();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("datasource", PrjConst.DATASOURCE);
        paramMap.put("token", account.getAccessToken());
        HttpResponse httpResponse = sendGetRequest(replaceBraces(PrjConst.CHAR_ORDER_URL,
                account.getId()), paramMap);
        String body = httpResponse.body();
        List<EveOrder> eveOrders = JSON.parseArray(body, EveOrder.class);
        for (EveOrder order :eveOrders) {
            int typeId = order.getTypeId();
            List<EveOrder> orderList = ret.get(typeId);
            if(orderList == null) {
                orderList = new ArrayList<>();
            }
            orderList.add(order);
            ret.put(typeId, orderList);
        }
        return ret;
    }

    public void parseStationExportMarket(AuthAccount account, String stationID) throws Exception {
        Map<Integer, List<EveOrder>> orderMap = getRfOrder(account.getAccessToken(), stationID);
        HashMap<Integer, Items> itemMap = getItemMap();
        ForkJoinPool pool = new ForkJoinPool();
        ParseOutputMarketTask task = new ParseOutputMarketTask(orderMap, itemMap);
        pool.invoke(task);
        Map<Integer, OrderParseResult> result = task.join();
        outExportSimple(result);
    }

    private void outExportSimple(Map<Integer, OrderParseResult> result) throws Exception {
        File file = new File("result/trade/exportParse");
        FileWriter writer = new FileWriter(file);

        List<Map.Entry<Integer,OrderParseResult>> list = new ArrayList<>(result.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Integer, OrderParseResult>>() {
            @Override
            public int compare(Map.Entry<Integer, OrderParseResult> o1, Map.Entry<Integer, OrderParseResult> o2) {
                Double sub = NumberUtil.sub(o2.getValue().getProfitByBuyOrder(), o1.getValue().getProfitByBuyOrder());
                return sub.intValue();
            }
        });
        DecimalFormat df = new DecimalFormat( "#,###.00");
        for (Map.Entry<Integer,OrderParseResult> item : list) {
            Integer id = item.getKey();
            OrderParseResult orderParseResult = item.getValue();
            String record = getExportRecord(df, orderParseResult, orderParseResult.getItem().getEnName());
            writer.write(record);
            writer.write("\r\n");
        }
        writer.flush();
        writer.close();
    }

    private String getExportRecord(DecimalFormat df, OrderParseResult orderParseResult, String enName) {
        StringBuilder sb = new StringBuilder();
        sb.append(enName);
        sb.append("\t");
        sb.append(df.format(orderParseResult.getMinPrice()));
        sb.append("\t");
        sb.append(df.format(orderParseResult.getEveMarketData().getSell().getMin()));
        sb.append("\t");
        sb.append(df.format(orderParseResult.getEveMarketData().getBuy().getMax()));
        return sb.toString();
    }

    public void parseStationMarket(AuthAccount account, AuthAccount jitaAccount, String stationID,
                                   int flowFilter, double hopeRoi, boolean onLackBPSearch) throws Exception {
        Map<Integer, List<EveOrder>> orderMap = getRfOrder(account.getAccessToken(), stationID);

//        List<EveOrder> orderList = orderMap.get(40362);
//        orderMap = pageOrderMap(orderMap, 1000, 2000);

        HashMap<Integer, Items> itemMap = getItemMap();
        HashMap<Integer, Integer> jitaInventory = getWarehouseMap(jitaAccount, PrjConst.STATION_ID_JITA_NAVY4);
        HashMap<Integer, Integer> rfInventory = getWarehouseMap(account, PrjConst.STATION_ID_RF_WINTERCO);
        HashMap<Integer, Integer> selfOrderMap = getSelfSellOrderRemainMap(account, PrjConst.STATION_ID_RF_WINTERCO);

        ForkJoinPool pool = new ForkJoinPool();
        ParseMarketTask task = new ParseMarketTask(selfOrderMap, jitaInventory, rfInventory, orderMap, itemMap,
                flowFilter, hopeRoi);
        pool.invoke(task);
        Map<Integer, OrderParseResult> result = task.join();

        System.out.println("得到拥有蓝图");
        List<CharBlueprint> ownBpID = getOwnBlueprint();
        List<Integer> fullIDList = getFullResearchIDList(ownBpID);
        List<Integer> manuTypeIDList =  getCanManuTypeID(fullIDList);
        System.out.println("输出推荐购买货物");
        outRecommendedSimple(result, manuTypeIDList);
        if(onLackBPSearch) {
            System.out.println("输出推荐购买蓝图");
            outLackBlueprint(ownBpID, result.keySet(), itemMap);
        }
    }

    private List<Integer> getCanManuTypeID(List<Integer> fullIDList) {
        IndustryActivityProductsMapper productMapper = getIndustryActivityProductsMapper();
        IndustryActivityProductsExample example = new IndustryActivityProductsExample();
        example.createCriteria().andBlueprinttypeidIn(fullIDList).andActivityidEqualTo(PrjConst.BLUEPRINT_ACTIVITY_TYPE_MANUFACTURING);
        List<IndustryActivityProducts> industryActivityProductsList = productMapper.selectByExample(example);
        List<Integer> ret = new ArrayList<>();
        for(IndustryActivityProducts products : industryActivityProductsList) {
            ret.add(products.getProducttypeid());
        }
        return ret;
    }

    private List<Integer> getFullResearchIDList(List<CharBlueprint> ownBpID) {
        List<Integer> ret = new ArrayList<>();
        for(CharBlueprint bp : ownBpID) {
            if(bp.getTimeEfficiency() >= 20 && bp.getMaterialEfficiency() >= 10) {
                ret.add(bp.getTypeId());
            }
        }
        return ret;
    }

    private void outLackBlueprint(List<CharBlueprint> ownBpID, Set<Integer> keySet, HashMap<Integer, Items> itemMap) throws Exception {
        List<Integer> BPOIDList = new ArrayList<>();
        for(CharBlueprint blueprint : ownBpID) {
            if(blueprint.getRuns() >= 0) {
                continue;
            }
            BPOIDList.add(blueprint.getTypeId());
        }
        System.out.println("得到缺少蓝图");
        List<Integer> needBpIDList = getNeedBpIDList(BPOIDList, keySet);

        System.out.println("输出缺少蓝图");
        File file = new File("result/trade/lackBlueprint");
        FileWriter writer = new FileWriter(file);

        for(Integer id : needBpIDList) {
            Items items = itemMap.get(id);
            if(items == null) {
                continue;
            }
            writer.write(items.getEnName());
            writer.write("\r\n");
        }
        writer.flush();
        writer.close();
    }

    private List<Integer> getNeedBpIDList(List<Integer> ownBpID, Set<Integer> keySet) {
        List<Integer> ret = new ArrayList<>();
        IndustryActivityProductsMapper productsMapper = getIndustryActivityProductsMapper();
        IndustryActivityProductsExample example = new IndustryActivityProductsExample();
        example.createCriteria().andProducttypeidIn(new ArrayList<>(keySet));
        List<IndustryActivityProducts> activityProductsList = productsMapper.selectByExample(example);
        for(IndustryActivityProducts products : activityProductsList) {
            if(ownBpID.contains(products.getBlueprinttypeid())) {
                continue;
            }
            ret.add(products.getBlueprinttypeid());
        }
        return ret;
    }

    private List<CharBlueprint> getOwnBlueprint() {
        AuthAccount account = new AuthAccount(PrjConst.SANJI_CHAR_ID, PrjConst.SANJI_CHAR_NAME,
                PrjConst.SANJI_REFRESH_TOKEN);
        List<CharBlueprint> bpList = new ArrayList<>();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("datasource", PrjConst.DATASOURCE);
        paramMap.put("page", 1);
        paramMap.put("token", account.getAccessToken());
        int cnt = 0;
        while (cnt < 3) {
            HttpResponse httpResponse = sendGetRequest(replaceBraces(PrjConst.GET_CHAR_BLUEPRINT_URL,
                    account.getId()), paramMap);
            int status = httpResponse.getStatus();
            if(status != 200) {
                cnt++;
                continue;
            }

            String body = httpResponse.body();
            bpList.addAll(JSON.parseArray(body, CharBlueprint.class));
            long maxPage = Long.parseLong(httpResponse.header("x-pages"));
            for (int i = 2; i <= maxPage; i ++) {
                Map<String, Object> paramMap2 = new HashMap<>();
                paramMap2.put("datasource", PrjConst.DATASOURCE);
                paramMap2.put("page", 1);
                paramMap2.put("token", account.getAccessToken());
                int cnt2 = 0;
                while (cnt2 < 3) {
                    HttpResponse httpResponse2 = sendGetRequest(replaceBraces(PrjConst.GET_CHAR_BLUEPRINT_URL,
                            account.getId()), paramMap);
                    int status2 = httpResponse2.getStatus();
                    if (status2 != 200) {
                        cnt2++;
                        continue;
                    }
                    bpList.addAll(JSON.parseArray(httpResponse2.body(), CharBlueprint.class));
                    break;
                }
            }
            break;
        }


        return bpList;
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

    private List<Integer> getAmmuSubMarketType() {
        List<Integer> idList = new ArrayList<>();
        MarketgroupsMapper marketgroupsMapper = getMarketgroupsMapper();

        MarketgroupsExample example = new MarketgroupsExample();
        example.createCriteria().andMarketgroupidEqualTo(DBConst.MARKET_GROUP_ROOT_AMMUNITION_CHARGES);
        List<Marketgroups> marketgroups = marketgroupsMapper.selectByExample(example);

        return idList;
    }

    private void outRecommendedSimple(Map<Integer, OrderParseResult> result, List<Integer> manuTypeIDList) throws IOException {
        File file = new File("result/trade/simple");
        FileWriter writer = new FileWriter(file);

        List<Map.Entry<Integer,OrderParseResult>> list = new ArrayList<>(result.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Integer, OrderParseResult>>() {
            @Override
            public int compare(Map.Entry<Integer, OrderParseResult> o1, Map.Entry<Integer, OrderParseResult> o2) {
                return o2.getValue().getStatisticData().getTotalProfit() - o1.getValue().getStatisticData().getTotalProfit();
            }
        });

        List<Map.Entry<Integer,OrderParseResult>> bpManuList = new ArrayList<>();
        for (Map.Entry<Integer,OrderParseResult> item : list) {
            Integer id = item.getKey();
            if(manuTypeIDList.contains(id)) {
                bpManuList.add(item);
                continue;
            }
            OrderParseResult orderParseResult = item.getValue();
            String record = getPurchaseRecord(orderParseResult, orderParseResult.getItem().getEnName());
            writer.write(record);
            writer.write("\r\n");
        }
        writer.write("===============manufacturing==============");
        writer.write("\r\n");
        for (Map.Entry<Integer,OrderParseResult> item : bpManuList) {
            OrderParseResult orderParseResult = item.getValue();
            String record = getPurchaseRecord(orderParseResult, orderParseResult.getItem().getEnName());
            writer.write(record);
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
        sb.append("\t");
        sb.append(orderParseResult.getStatisticData().getProfit());
        sb.append("\t");
        sb.append(orderParseResult.getStatisticData().getProfitMargin());
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
            double jitaSellMin = orderParseResult.getEveMarketData().getSell().getMin();
            double min = jitaSellMin == 0 ? Long.MAX_VALUE :
                    orderParseResult.getEveMarketData().getSell().getMin();
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

    private HashMap<Integer, Integer> getSelfSellOrderRemainMap(AuthAccount account, String locationID) {
        HashMap<Integer, Integer>ret = new HashMap<>();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("datasource", PrjConst.DATASOURCE);
        paramMap.put("token", account.getAccessToken());
        HttpResponse httpResponse = sendGetRequest(replaceBraces(PrjConst.CHAR_ORDER_URL,
                account.getId()), paramMap);
        String body = httpResponse.body();
        List<EveOrder> eveOrders = JSON.parseArray(body, EveOrder.class);
        for (EveOrder order :eveOrders) {
            if(order.isBuyOrder()) {
                continue;
            }
            if(!locationID.equals(order.getLocationId())) {
                continue;
            }
            int typeId = order.getTypeId();
            Integer remain = ret.get(typeId);
            if(remain == null) {
                remain = 0;
            }
            remain += order.getVolumeRemain();
            ret.put(typeId, remain);
        }
        return ret;
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
    private HashMap<Integer, Integer> getWarehouseMap(AuthAccount account, String locationID) throws Exception {
        List<AssertItem> anAssert = getAssert(account);
        HashMap<Integer, Integer> ret = new HashMap<>();
        for(AssertItem item : anAssert) {
            if(Long.parseLong(locationID) != item.getLocationId()) {
                continue;
            }
            int typeId = item.getTypeId();
            Integer remain = ret.get(typeId);
            if(remain == null) {
                remain = 0;
            }
            remain += item.getQuantity();
            ret.put(typeId, remain);
        }
        return ret;
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
}
