package com.eve.util;


import cn.hutool.core.util.NumberUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.eve.dao.ItemsMapper;
import com.eve.entity.EveMarketData;
import com.eve.entity.EveMarketSellOrder;
import com.eve.entity.Fitting;
import com.eve.entity.OrderParseResult;
import com.eve.entity.database.Items;
import com.eve.entity.database.ItemsExample;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class TradeUtil {
    public Fitting parseFittingText(String fitting) {
        Fitting ship = new Fitting();
        String[] content = fitting.split("\n");
        String intro = content[0];
        int i1 = intro.indexOf("[");
        int i2 = intro.indexOf(",");
        int i3 = intro.indexOf("]");
        ship.setName(intro.substring(i1 + 1, i2).trim());
        ship.setAlias(intro.substring(i2 + 1, i3).trim());
        boolean flag = true;
        int phase = 0;
        for (int i = 1; i < content.length; i++) {
            String line = content[i];
            if("".equals(line)) {
                flag = true;
                continue;
            }
            if (flag == true) {
                if(phase++ >= 4) {
                    return ship;
                }
                flag = false;
            }
            setFittingAttr(ship, line, phase);
        }
        return ship;
    }

    private void setFittingAttr(Fitting ship, String line, int phase) {
        switch (phase) {
            case 1 :
                ship.addLow(line);
                break;
            case 2 :
                ship.addMiddle(line);
                break;
            case 3 :
                ship.addHigh(line);
                break;
            case 4 :
                ship.addRigging(line);
                break;
        }
    }

    public Map<Integer, EveMarketData> queryJitaOrderInfo(List<Integer> typeIDs) {
        Map<Integer, EveMarketData> ret = new HashMap<>();
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("typeid", listToString(typeIDs));
        paramMap.put("usesystem", PrjConst.STATION_ID_JITA);
        String result= HttpUtil.get("https://api.evemarketer.com/ec/marketstat/json", paramMap);
////        String result="[{\"buy\":{\"forQuery\":{\"bid\":true,\"types\":[200],\"regions\":[],\"systems\":[]," +
////                "\"hours\":24,\"minq\":1},\"volume\":10097500,\"wavg\":41.28,\"avg\":67.68,\"variance\":1680.61,\"stdDev\":41.00,\"median\":20.03,\"fivePercent\":115.76,\"max\":140.00,\"min\":8.50,\"highToLow\":true,\"generated\":1587714956485},\"sell\":{\"forQuery\":{\"bid\":false,\"types\":[200],\"regions\":[],\"systems\":[],\"hours\":24,\"minq\":1},\"volume\":18381452,\"wavg\":227.89,\"avg\":231.06,\"variance\":6335.78,\"stdDev\":79.60,\"median\":207.70,\"fivePercent\":161.32,\"max\":799.00,\"min\":80.00,\"highToLow\":false,\"generated\":1587714956485}},{\"buy\":{\"forQuery\":{\"bid\":true,\"types\":[201],\"regions\":[],\"systems\":[],\"hours\":24,\"minq\":1},\"volume\":9767443,\"wavg\":100.94,\"avg\":106.07,\"variance\":1381.71,\"stdDev\":37.17,\"median\":100.50,\"fivePercent\":152.06,\"max\":178.00,\"min\":20.03,\"highToLow\":true,\"generated\":1587716159714},\"sell\":{\"forQuery\":{\"bid\":false,\"types\":[201],\"regions\":[],\"systems\":[],\"hours\":24,\"minq\":1},\"volume\":28857599,\"wavg\":223.70,\"avg\":223.59,\"variance\":4491.63,\"stdDev\":67.02,\"median\":210.99,\"fivePercent\":154.98,\"max\":500.00,\"min\":99.68,\"highToLow\":false,\"generated\":1587716159714}}]";
        JSONArray array = JSON.parseArray(result);
        int size = array.size();
        for (int i = 0; i < size; i++) {
            EveMarketData object = array.getObject(i, EveMarketData.class);
            ret.put(Integer.parseInt(object.getBuy().getForQuery().getTypes().get(0)), object);
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

    private ItemsMapper getItemsMapper() throws IOException {
        String mybatisConfigPath = "mybatisconfig.xml";
        InputStream inputStream = Resources.getResourceAsStream(mybatisConfigPath);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        return sqlSession.getMapper(ItemsMapper.class);
    }

    public void getOasaMarketRecommendedPurchaseList(String folderPath, String orderListPath, String rfWareHousePath,
                                                     String jitaWarehousePath, int hopeProfit, double profitMargin
                                                     ) throws Exception {
        Map<Integer, OrderParseResult> result = new HashMap<>();
        ItemsMapper itemsMapper = getItemsMapper();
        parseOasaOrder(result, folderPath, itemsMapper);
        setJitaItemInfo(result);
        importInventory(result, orderListPath, rfWareHousePath, jitaWarehousePath, itemsMapper);
        getRecommendedPurchaseQuantity(result, hopeProfit, profitMargin);
        Iterator<Integer> iterator = result.keySet().iterator();
        while (iterator.hasNext()) {
            Integer next = iterator.next();
            System.out.println(result.get(next));
        }
    }

    private void getRecommendedPurchaseQuantity(Map<Integer, OrderParseResult> result, int hopeProfit, double hopeMargin) throws IOException {
        computeFilterProfit(result, hopeProfit, hopeMargin);
//        outRecommendedDetailCSV();
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
            writer.write("-------------------------");
            writer.write("\r\n");
            writer.write("---------monopoly--------");
            writer.write("\r\n");
            writer.write("-------------------------");
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

    private void computeFilterProfit(Map<Integer, OrderParseResult> result, int hopeProfit,
                                              double hopeMargin) {
        Iterator<Integer> iter = result.keySet().iterator();
        while(iter.hasNext()) {
            Integer id = iter.next();
            OrderParseResult parseResult = result.get(id);
            parseResult.computerProfit();
            if(!parseResult.isMonopoly() && (parseResult.getStatisticData().getProfit() < hopeProfit || parseResult.getStatisticData().getProfitMargin() < hopeMargin)) {
//                result.remove(id);
                iter.remove();
                continue;
            }
            parseResult.predictPurchaseCount();
            if (parseResult.getRecommendedCount() <= 0) {
//                result.remove(id);
                iter.remove();
            }
        }
    }

    private void importInventory(Map<Integer, OrderParseResult> result, String orderListPath, String rfWareHousePath,
                                 String jitaWarehousePath, ItemsMapper itemsMapper) throws IOException {
        if(orderListPath != null) {
            importOrderList(result, orderListPath);
        }
        if(rfWareHousePath != null) {
            importWarehouse(result, rfWareHousePath, itemsMapper);
        }
        if(jitaWarehousePath != null) {
            importWarehouse(result, jitaWarehousePath, itemsMapper);
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

    private void writeCSV(List<OrderParseResult> result) throws Exception {
        Appendable out = new PrintWriter("out.csv");
        CSVPrinter csvPrinter = CSVFormat.DEFAULT.print(out);
        for (OrderParseResult order : result) {
//            System.out.println(order);
            Items item = order.getItem();
            EveMarketSellOrder sell = order.getEveMarketData().getSell();
            csvPrinter.printRecord(order.getTypeID(), item.getCnName(), item.getEnName(),
                    item.getVolumn(), order.getMinPrice(), order.getMaxPrice(), order.getAverage(),
                    order.getVolRemain(), sell.getMin(), sell.getMax(), sell.getAvg(), sell.getVariance(),
                    sell.getStdDev(), sell.getMedian(), sell.getVolume());
        }

//        CSVPrinter csvPrinter = CSVFormat.DEFAULT.print(out);
//                csvPrinter.printRecord(id, name.get("zh"), name.get("en"), item.get("volume"));
        csvPrinter.flush();
        csvPrinter.close();
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

    private Map<Integer, Items> getItemInfoMap(List<Integer> idList, ItemsMapper itemsMapper) throws IOException {
        Map<Integer, Items> map = new HashMap<>();
        ItemsExample example = new ItemsExample();
        example.createCriteria().andIdIn(idList);
        List<Items> items = itemsMapper.selectByExample(example);
        for (Items item : items) {
            map.put(item.getId(), item);
        }
        return map;
    }

    private List<Integer> getItemIDList(List<OrderParseResult> orderParseResults) {
        List<Integer> result = new ArrayList<>();
        for(OrderParseResult order : orderParseResults) {
            result.add(order.getTypeID());
        }
        return result;
    }
    public static void main(String[] args) throws Exception {
        TradeUtil tradeUtil = new TradeUtil();
        tradeUtil.getOasaMarketRecommendedPurchaseList("D:\\\\yhy\\\\doc\\\\eve4.24\\\\test", "D:\\yhy\\doc\\record" +
                        "\\My Orders-2020.04.26 2233.txt", "D:\\yhy\\doc\\record\\inventory11.txt", null, 1,
                0.01);
//        tradeUtil.getOasaMarketRecommendedPurchaseList("D:\\yhy\\doc\\eve4.24\\test");
    }

    public void parseOasaOrder(Map<Integer, OrderParseResult> result, String folderPath, ItemsMapper itemsMapper) throws Exception {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        for(File file : files) {
            String name = file.getName();
            String en_name = name.split("-")[1];
            boolean order = name.endsWith(".txt");
            Items item = getItemByEnName(itemsMapper, en_name);
            if(item == null) {
                continue;
            }
            OrderParseResult parseResult = result.get(item.getId());
            if(parseResult == null) {
                parseResult = new OrderParseResult();
                parseResult.setTypeID(item.getId());
                parseResult.setItem(item);
            }
            if(order) {
                setOrderInfo(parseResult, file);
            } else {
                setHisInfo(parseResult, file);
            }
            result.put(item.getId(), parseResult);
        }
    }

    private void setHisInfo(OrderParseResult parseResult, File file) {
        BufferedReader reader = null;
        try {
//            System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 0;
            // 一次读入一行，直到读入null为文件结束
            int sumCnt = 0;
            double sumSell = 0.0;
            while ((tempString = reader.readLine()) != null) {
                // 显示行号
                if(++line == 1) {
                    continue;
                }
                if(line > 8) {
                    break;
                }
//                System.out.println("line " + line + ": " + tempString);
//                line++;

                String[] split = tempString.split("\t");
                int dayCnt = Integer.parseInt(split[2]);
//                double dayAvg = Double.parseDouble(split[5].replace("ISK", "").replace(",", ""));
                sumCnt += dayCnt;
            }
            if(line == 1) {
                return;
            }
            BigDecimal dailyCnt = NumberUtil.round(NumberUtil.div(sumCnt, (line - 2)), 1,
                    RoundingMode.DOWN);
            parseResult.setDailySalesVolume(dailyCnt.doubleValue());
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

    private void setOrderInfo(OrderParseResult parseResult, File file) throws Exception {
        Reader in = new FileReader(file);
        double minPrice = Long.MAX_VALUE;
        double maxPrice = 0.0;
        double sum = 0;
        int volRemain = 0;
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
        Iterator<CSVRecord> iter = records.iterator();
        if(!iter.hasNext()) {
            return;
        }
        for (CSVRecord record : records) {
            if(!"0".equals(record.get("jumps")) || "True".equals(record.get("bid"))) {
                continue;
            }
            double price = Double.parseDouble(record.get("price"));
            if(price > maxPrice) {
                maxPrice = price;
            }
            if(price < minPrice) {
                minPrice = price;
            }
            double volRemaining = Double.parseDouble(record.get("volRemaining"));
            sum += price * volRemaining;
            volRemain += volRemaining;
            parseResult.setTypeID(Integer.parseInt(record.get("typeID")));
        }
        parseResult.setMaxPrice(maxPrice);
        parseResult.setMinPrice(minPrice);
        parseResult.setVolRemain(volRemain);
        BigDecimal round = volRemain == 0 ? new BigDecimal(0) : NumberUtil.round(sum / volRemain, 2,
                RoundingMode.DOWN);
        parseResult.setAverage(round.doubleValue());
    }
}
