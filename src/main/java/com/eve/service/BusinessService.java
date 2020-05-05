package com.eve.service;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.eve.dao.ItemsMapper;
import com.eve.entity.EveMarketData;
import com.eve.entity.EveOrder;
import com.eve.entity.OrderHistory;
import com.eve.entity.OrderParseResult;
import com.eve.entity.database.Items;
import com.eve.entity.database.ItemsExample;
import com.eve.util.PrjConst;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.*;
import java.util.*;

public class BusinessService {
    public static void main(String[] args) throws Exception {
        BusinessService bs = new BusinessService();
        bs.parseStationMarket(PrjConst.STATION_ID_RF_WINTERCO, null, 400000,
                0.4);
    }

    private String getAccessToken() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("grant_type", "refresh_token");
        paramMap.put("refresh_token", PrjConst.REFRESH_TOKEN);
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

    private List<EveOrder> getRfOrder(String accessToken, String stationID) {
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
            List<EveOrder> eveOrders2 = JSON.parseArray(body2, EveOrder.class);
            eveOrders.addAll(eveOrders2);
        }
        return eveOrders;
    }

    public void parseStationMarket(String stationID, String orderListPath,
                                   int hopeProfit, double profitMargin) throws Exception {
        String accessToken = getAccessToken();
        //TODO 根据TypeID进行分类 进行整理
        List<EveOrder> rfOrder = getRfOrder(accessToken, stationID);
        //TODO 分次进行，同时解析Jita订单时注意返回错误格式的处理办法
        rfOrder = rfOrder.subList(100, 200);
        Map<Integer, OrderParseResult> result = new HashMap<>();
        inputRfOrder(result, rfOrder);
        setJitaItemInfo(result);
        ItemsMapper itemsMapper = getItemsMapper();
        importInventory(result, orderListPath, itemsMapper);
        getRecommendedPurchaseQuantity(result, hopeProfit, profitMargin, itemsMapper);
    }

    private void getRecommendedPurchaseQuantity(Map<Integer, OrderParseResult> result, int hopeProfit, double hopeMargin, ItemsMapper itemsMapper) throws IOException {
        filterItemAndGetDailyVolumn(result, hopeProfit, hopeMargin);
        getItemInfo(result, itemsMapper);
        computeFilterProfit(result, hopeProfit, hopeMargin);
        outRecommendedSimple(result);
    }

    private void outRecommendedSimple(Map<Integer, OrderParseResult> result) throws IOException {
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
            String record = getPurchaseRecord(orderParseResult);
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
            writer.write(getPurchaseRecord(mono));
            writer.write("\r\n");
        }
        writer.flush();
        writer.close();
    }

    private String getPurchaseRecord(OrderParseResult orderParseResult) {
        StringBuilder sb = new StringBuilder();
        sb.append(orderParseResult.getItem().getEnName());
        sb.append(" x");
        sb.append(orderParseResult.getRecommendedCount());
        return sb.toString();
    }

    private void getItemInfo(Map<Integer, OrderParseResult> result, ItemsMapper itemsMapper) {
        Iterator<Integer> iter = result.keySet().iterator();
        while (iter.hasNext()) {
            Integer typeID = iter.next();
            OrderParseResult orderParseResult = result.get(typeID);
            Items item = itemsMapper.selectByPrimaryKey(typeID);
            orderParseResult.setItem(item);
        }
    }

    private void filterItemAndGetDailyVolumn(Map<Integer, OrderParseResult> result, int hopeProfit, double hopeMargin) {
        Iterator<Integer> iter = result.keySet().iterator();
        while (iter.hasNext()) {
            Integer typeID = iter.next();
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
                                     double hopeMargin) {
        Iterator<Integer> iter = result.keySet().iterator();
        while(iter.hasNext()) {
            Integer id = iter.next();
            OrderParseResult parseResult = result.get(id);
            parseResult.computerProfit();
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
                                 ItemsMapper itemsMapper) throws IOException {
        if(orderListPath != null) {
            importOrderList(result, orderListPath);
        }
        importWarehouse(result, PrjConst.PATH_JITA_INVENTORY, itemsMapper);
        importWarehouse(result, PrjConst.PATH_RF_INVENTORY, itemsMapper);
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

    private Items getItemByEnName(ItemsMapper itemsMapper, String en_name) {
        ItemsExample example = new ItemsExample();
        example.createCriteria().andEnNameEqualTo(en_name);
        List<Items> items = itemsMapper.selectByExample(example);
        if(items.size() == 0) {
            return null;
        }
        return items.get(0);
    }

    private void importWarehouse(Map<Integer, OrderParseResult> result, String rfWareHousePath, ItemsMapper itemsMapper) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(new File(rfWareHousePath)));
            String tempString = null;
            while ((tempString = reader.readLine()) != null) {
                String[] split = tempString.split("\t");
                String en_name = split[0];
                Items item = getItemByEnName(itemsMapper, en_name.trim());
                if(item == null) {
                    continue;
                }
                OrderParseResult orderParseResult = result.get(item.getId());
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

    private Map<Integer, EveMarketData> queryJitaOrderInfo(List<Integer> typeIDs) {
        Map<Integer, EveMarketData> ret = new HashMap<>();
        for (int i = 0; i < typeIDs.size(); i = i + 200) {
            int end = NumberUtil.min(i + 200, typeIDs.size());
            List<Integer> sub = typeIDs.subList(i, end);

            HashMap<String, Object> paramMap = new HashMap<>();
            paramMap.put("typeid", listToString(sub));
            paramMap.put("usesystem", PrjConst.STATION_ID_JITA);
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

    private void inputRfOrder(Map<Integer, OrderParseResult> result, List<EveOrder> rfOrder) {
        for(EveOrder order : rfOrder) {
            if(order.isBuyOrder()) {
                continue;
            }
            int typeId = order.getTypeId();
            OrderParseResult orderParseResult = result.get(typeId);
            if(orderParseResult == null) {
                orderParseResult = new OrderParseResult();
            }
            orderParseResult.setTypeID(typeId);
            if(order.getPrice() < orderParseResult.getMinPrice()) {
                orderParseResult.setMinPrice(order.getPrice());
            }
            if(order.getPrice() > orderParseResult.getMaxPrice()) {
                orderParseResult.setMaxPrice(order.getPrice());
            }
            //TODO 平均值未设置，是否添加有待考察
            orderParseResult.setVolRemain(orderParseResult.getVolRemain() + order.getVolumeRemain());
            result.put(typeId, orderParseResult);
        }
    }


}
