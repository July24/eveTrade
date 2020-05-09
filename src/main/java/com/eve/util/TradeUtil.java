package com.eve.util;


import cn.hutool.core.util.NumberUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.eve.dao.ItemsMapper;
import com.eve.entity.*;
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

    private Map<Integer, EveMarketData> queryJitaOrderInfo(List<Integer> typeIDs) {
        Map<Integer, EveMarketData> ret = new HashMap<>();
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("typeid", listToString(typeIDs));
        paramMap.put("usesystem", PrjConst.SOLAR_SYSTEM_ID_JITA);
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

    private void computeFilterProfit(Map<Integer, OrderParseResult> result, int hopeProfit,
                                              double hopeMargin) {
        Iterator<Integer> iter = result.keySet().iterator();
        while(iter.hasNext()) {
            Integer id = iter.next();
            OrderParseResult parseResult = result.get(id);
            parseResult.computerProfit();
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
                    item.getVolume(), order.getMinPrice(), order.getMaxPrice(), order.getAverage(),
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

    private void parseOasaOrder(Map<Integer, OrderParseResult> result, ItemsMapper itemsMapper) throws Exception {
        File folder = new File(PrjConst.PATH_MARKET_DATA_FILE_FOLDER);
        File[] files = folder.listFiles();
        for(File file : files) {
            String name = file.getName();
            int bgn = name.indexOf("-");
            int end = name.lastIndexOf("-");
            String en_name = name.substring(bgn+1, end);
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

    public void getInventoryRelist(String orderFilePath) throws Exception {
        ItemsMapper itemsMapper = getItemsMapper();
        File orderFile = new File(orderFilePath);
        File inventoryFile = new File(PrjConst.PATH_RF_INVENTORY);

        List<Integer> orderIDList = new ArrayList<>();
        Reader in = new FileReader(orderFile);
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
        for (CSVRecord record : records) {
            if("True".equalsIgnoreCase(record.get("bid"))) {
                continue;
            }
            orderIDList.add(Integer.parseInt(record.get("typeID")));
        }

        List<String> enNameList = new ArrayList<>();
        BufferedReader reader = null;
        reader = new BufferedReader(new FileReader(inventoryFile));
        String tempString = null;
        while ((tempString = reader.readLine()) != null) {
            String[] split = tempString.split("\t");
            enNameList.add(split[0].trim());
        }
        reader.close();
        ItemsExample example = new ItemsExample();
        example.createCriteria().andEnNameIn(enNameList);
        List<Items> items = itemsMapper.selectByExample(example);
        items.removeIf(next -> orderIDList.contains(next.getId()));

        outRelist(items);
    }

    private void outRelist(List<Items> items) throws IOException {
        File file = new File("result/trade/InventoryRelist");
        FileWriter writer = new FileWriter(file);
        Iterator<Items> iter = items.iterator();
        while (iter.hasNext()) {
            Items id = iter.next();
            writer.write(id.getEnName());
            writer.write("\r\n");
        }
        writer.flush();
        writer.close();
    }

    public void revisionBuyOrder(int hopeProfit, double profitMargin) throws Exception {
        Map<Integer, OrderParseResult> map = loadTemp();
        ItemsMapper itemsMapper = getItemsMapper();
        List<BuyOrderRecord> list = parseBuyOrder(itemsMapper);
        if(revisionBuyCount(list, map, hopeProfit, profitMargin)) {
            outRevisionBuyOrder(list);
        }
    }

    private void outRevisionBuyOrder(List<BuyOrderRecord> list) throws IOException {
        File file = new File("result/trade/revisionBuy/revisionBuyOrder");
        FileWriter writer = new FileWriter(file);
        for (BuyOrderRecord record : list) {
            StringBuilder sb = new StringBuilder();
            sb.append(record.getEn_name());
            sb.append(" x");
            sb.append(record.getCount());
            writer.write(sb.toString());
            writer.write("\r\n");
        }
        writer.flush();
        writer.close();
    }

    private boolean revisionBuyCount(List<BuyOrderRecord> list, Map<Integer, OrderParseResult> map, int hopeProfit,
                               double profitMargin) {
        boolean need = false;
        for (BuyOrderRecord record : list) {
            if(record.getPrice() == 0) {
                record.setCount(reduceBuyCount(record.getCount()));
                need = true;
            } else {
                OrderParseResult orderParseResult = map.get(record.getId());
                double profit =
                        (orderParseResult.getMinPrice() - record.getPrice() - PrjConst.EXPRESS_FAX_CUBIC_METRES * orderParseResult.getItem().getVolume()) * (1 - PrjConst.AVG_BROKER_FAX) * (1 - PrjConst.SELL_FAX);
                double margin = NumberUtil.div(profit, record.getPrice(), 2);
                if(profit < hopeProfit && margin < hopeProfit) {
                    record.setCount(reduceBuyCount(record.getCount()));
                    need = true;
                }
            }
        }
        return need;
    }

    private int reduceBuyCount(int count) {
        return NumberUtil.roundDown(NumberUtil.mul(count, 0.8), 0).intValue();
    }

    private List<BuyOrderRecord> parseBuyOrder(ItemsMapper itemsMapper) throws Exception {
        List<BuyOrderRecord> ret = new ArrayList<>();
        BufferedReader reader = null;
        reader = new BufferedReader(new FileReader("result/trade/revisionBuy/buyOrderOut"));
        String tempString = null;
        while ((tempString = reader.readLine()) != null) {
            String[] split = tempString.split("\t");
//            enNameList.add(split[0].trim());
            String en_name = split[0];
            if("Total:".equalsIgnoreCase(en_name)) {
                continue;
            }
            double price = "-".equals(split[2]) ? 0 : Double.parseDouble(split[2].replace(",", ""));
            BuyOrderRecord record = new BuyOrderRecord();
            Items item = getItemByEnName(itemsMapper, en_name.trim());
            record.setId(item.getId());
            record.setEn_name(en_name.trim());
            record.setCount(Integer.parseInt(split[1]));
            record.setPrice(price);
            ret.add(record);
        }
        reader.close();
        return ret;
    }

    private Map<Integer, OrderParseResult> loadTemp() throws Exception {
        File file = new File("result/trade/local/temp");
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
        return (Map<Integer, OrderParseResult>)ois.readObject();
    }

    private Map<String, Integer> getRecommendMap() throws Exception {
        Map<String, Integer> map = new HashMap<>();
        BufferedReader reader = null;
        reader = new BufferedReader(new FileReader(PrjConst.PATH_RECOMMEND_BUY_SIMPLE));
        String tempString = null;
        while ((tempString = reader.readLine()) != null) {
            if(tempString.startsWith(PrjConst.SEPARATOR_PREFIX)) {
                continue;
            }
            int x = tempString.lastIndexOf("x");
            map.put(tempString.substring(0, x-1), Integer.parseInt(tempString.substring(x+1)));
        }
        reader.close();
        return map;
    }

    private Map<String, Integer> getJitaInventoryMap() throws Exception {
        Map<String, Integer> map = new HashMap<>();
        BufferedReader reader = null;
        reader = new BufferedReader(new FileReader(PrjConst.PATH_JITA_INVENTORY));
        String tempString = null;
        while ((tempString = reader.readLine()) != null) {
            String[] split = tempString.split("\t");
            map.put(split[0], Integer.parseInt(split[1].replace(",", "")));
        }
        reader.close();
        return map;
    }

    private Map<String, Integer> getOrderMap(String orderFilePath) throws Exception {
        ItemsMapper itemsMapper = getItemsMapper();
        Map<String, Integer> map = new HashMap<>();
        if(orderFilePath == null) {
            return map;
        }
        Reader in = new FileReader(new File(orderFilePath));
        Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(in);
        for (CSVRecord record : records) {
            if("False".equalsIgnoreCase(record.get("bid"))) {
                continue;
            }
            int typeID = Integer.parseInt(record.get("typeID"));
            Items items = itemsMapper.selectByPrimaryKey(typeID);
//            String volRemaining = record.get("volRemaining");
            String volEntered = record.get("volEntered");
            Double dEntered = Double.parseDouble(volEntered);
//            NumberUtil.sub(volEntered, volRemaining).intValue()
            map.put(items.getEnName(), dEntered.intValue());
        }
        return map;
    }

    public void getRecommendNotBuyList(String orderFilePath) throws Exception {
        Map<String, Integer> recommendMap = getRecommendMap();
        Map<String, Integer> jitaMap = getJitaInventoryMap();
        Map<String, Integer> orderMap = getOrderMap(orderFilePath);
        Iterator<String> iter = recommendMap.keySet().iterator();
        while (iter.hasNext()) {
            String enName = iter.next();
            Integer count = recommendMap.get(enName);
            Integer inventoryRemain = jitaMap.get(enName) == null ? 0 : jitaMap.get(enName);
            Integer orderBuy = orderMap.get(enName) == null ? 0 : orderMap.get(enName);
            int remainBuy = count - inventoryRemain - orderBuy;
            if(remainBuy <= 0) {
                iter.remove();
            } else {
                recommendMap.put(enName, remainBuy);
            }
        }
        outNotBuyList(recommendMap);
    }

    private void outNotBuyList(Map<String, Integer> recommendMap) throws Exception {
        File file = new File(PrjConst.PATH_RECOMMEND_NOT_BUY);
        FileWriter writer = new FileWriter(file);
        Iterator<String> iter = recommendMap.keySet().iterator();
        while (iter.hasNext()) {
            String enName = iter.next();
            StringBuilder sb = new StringBuilder();
            sb.append(enName);
            sb.append(" x");
            sb.append(recommendMap.get(enName));
            writer.write(sb.toString());
            writer.write("\r\n");
        }
        writer.flush();
        writer.close();
    }
}
