package com.eve.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eve.dao.IndustryactivitymaterialsMapper;
import com.eve.dao.IndustryactivityproductsMapper;
import com.eve.dao.InvmarketgroupsMapper;
import com.eve.dao.ItemsMapper;
import com.eve.entity.*;
import com.eve.entity.database.*;
import com.eve.util.DBConst;
import com.eve.util.PrjConst;
import com.eve.util.TradeUtil;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class IndustryService extends ServiceBase {
    private static double MATERIAL_RESEARCH = 0.1;

    public static void main(String[] args) throws Exception {
        AuthAccount account = new AuthAccount(PrjConst.ALLEN_CHAR_ID, PrjConst.ALLEN_CHAR_NAME, PrjConst.ALLEN_REFRESH_TOKEN);
        AuthAccount sanjiAccount = new AuthAccount(PrjConst.SANJI_CHAR_ID, PrjConst.SANJI_CHAR_NAME,
                PrjConst.SANJI_REFRESH_TOKEN);

        IndustryService is = new IndustryService();
//        Map<String, Integer> productMap = new HashMap<>();
//        productMap.put("Proton M",114288);
//        productMap.put("Fusion M",15000);
//        productMap.put("425mm AutoCannon I",10);
//        is.getListNeedMaterial(productMap, sanjiAccount);
        String productStr = "Small Memetic Algorithm Bank I\t8\n" +
                "Medium Capacitor Control Circuit I\t120\n" +
                "Medium Cargohold Optimization I\t96\n" +
                "Large Hyperspatial Velocity Optimizer I\t12";
        is.getListNeedMaterial(productStr, sanjiAccount);
//        is.getManufacturingList(account, 0.1);
//        is.getBlueprintBuyList();
    }

    private void getBlueprintBuyList() throws IOException {
        List<Integer> allRigsMG = getAllRigsMarketGroup();
        ItemsMapper itemsMapper = getItemsMapper();
        List<Items> allRigsItem = getAllRigsItem(allRigsMG, itemsMapper);

        List<CharBlueprint> ownBpID = getOwnBlueprint();
        List<Integer> blueIDList = getBlueIDList(ownBpID);
        List<Integer> manuTypeIDList =  getCanManuTypeID(blueIDList);
        List<OrderParseResult> orderParseResults = filterRigsVolume(allRigsItem, manuTypeIDList);
        outBlueprintBuyList(orderParseResults);
    }

    private void outBlueprintBuyList(List<OrderParseResult> orderParseResults) throws IOException {
        File file = new File("result/industry/blueprintBuyList");
        FileWriter writer = new FileWriter(file);

        orderParseResults.sort(new Comparator<OrderParseResult>() {
            @Override
            public int compare(OrderParseResult o1, OrderParseResult o2) {
                return BigDecimal.valueOf(o2.getDailySalesVolume() * 100 - o1.getDailySalesVolume() * 100).intValue();
            }
        });
        for (OrderParseResult result : orderParseResults) {
            String enName = result.getItem().getEnName();
            StringBuilder sb = new StringBuilder();
            sb.append(enName);
            sb.append("\t");
            sb.append(NumberUtil.round(result.getDailySalesVolume(), 0, RoundingMode.UP));
            writer.write(sb.toString());
            writer.write("\r\n");
        }
        writer.flush();
        writer.close();
    }

    private List<OrderParseResult> filterRigsVolume(List<Items> allRigsItem, List<Integer> manuTypeIDList) {
        List<OrderParseResult> ret = new ArrayList<>();
        for(Items rig : allRigsItem) {
            System.out.println("计算ID：" + rig.getId());
            if(manuTypeIDList.contains(rig.getId())) {
                continue;
            }
            OrderParseResult orderParseResult = new OrderParseResult();
            orderParseResult.setDailySalesVolume(TradeUtil.getDailyVolume(rig.getId()));
            orderParseResult.setItem(rig);
            ret.add(orderParseResult);
        }
        return ret;
    }

    private List<Integer> getBlueIDList(List<CharBlueprint> ownBpID) {
        List<Integer> list = new ArrayList<>();
        for(CharBlueprint blueprint : ownBpID) {
            list.add(blueprint.getTypeId());
        }
        return list;
    }

    private List<Items> getAllRigsItem(List<Integer> allRigsMG, ItemsMapper itemsMapper) {
        ItemsExample example = new ItemsExample();
        example.createCriteria().andMarketgroupidIn(allRigsMG).andMetagroupidIsNull();
        return itemsMapper.selectByExample(example);
    }

    private List<Integer> getAllRigsMarketGroup() {
        List<Integer> allSub = new ArrayList<>();
        InvmarketgroupsMapper mapper = getMarketgroupsMapper();
        InvmarketgroupsExample example = new InvmarketgroupsExample();
        example.createCriteria().andParentgroupidEqualTo(DBConst.MARKET_GROUP_TYPE_RIGS);
        List<Invmarketgroups> invmarketgroups = mapper.selectByExample(example);
        List<Integer> second = new ArrayList<>();
        for(Invmarketgroups invmarketgroup : invmarketgroups) {
            second.add(invmarketgroup.getMarketgroupid());
        }
        InvmarketgroupsExample another = new InvmarketgroupsExample();
        another.createCriteria().andParentgroupidIn(second);
        List<Invmarketgroups> sub = mapper.selectByExample(another);
        for(Invmarketgroups invmarketgroup : sub) {
            allSub.add(invmarketgroup.getMarketgroupid());
        }
        return allSub;
    }

    public void getManufacturingList(AuthAccount rfAccount,
                                     double hopeMargin) throws Exception {
        Map<Integer, List<EveOrder>> myOrder = getMyOrder(rfAccount);
        List<AssertItem> anAssert = getAssert(rfAccount);
        List<AssertItem> rfAssert = filterRFAssert(anAssert, new ArrayList<>(),
                PrjConst.STATION_ID_RF_WINTERCO);
        ItemsMapper itemsMapper = getItemsMapper();
        Map<Integer, IndustryProduct> productMap = getProductIDList();
        assembleStationPrice(rfAccount, productMap);
        assemblePurchasePrice(productMap, itemsMapper);
        filterMargin(productMap, hopeMargin, myOrder, rfAssert);
        outRecommendedManufacturing(productMap, itemsMapper);
    }

    private void outRecommendedManufacturing(Map<Integer, IndustryProduct> productMap, ItemsMapper itemsMapper) throws Exception {
        File file = new File("result/industry/recommendManufacturing");
        FileWriter writer = new FileWriter(file);
        Iterator<Integer> iter = productMap.keySet().iterator();
        while (iter.hasNext()) {
            Integer id = iter.next();
            IndustryProduct industryProduct = productMap.get(id);
            StringBuilder sb = new StringBuilder();
            sb.append(itemsMapper.selectByPrimaryKey(id).getEnName());
            writer.write(sb.toString());
            writer.write("\t");
//            writer.write("x");
            writer.write(new BigDecimal(industryProduct.getDailyVolume() * 4).toString());
            writer.write("\r\n");
        }
        writer.flush();
        writer.close();
    }

    private void filterMargin(Map<Integer, IndustryProduct> productMap, double hopeMargin, Map<Integer,
            List<EveOrder>> myOrder, List<AssertItem> rfAssert) {
        Iterator<Integer> iter = productMap.keySet().iterator();
        while(iter.hasNext()) {
            Integer id = iter.next();
            IndustryProduct product = productMap.get(id);
            double flowStationPrice = product.getStationPrice() * product.getFlowQuantity();
            double purchasePrice = product.getPurchasePrice();
            double margin = (flowStationPrice - purchasePrice)/purchasePrice;
            if(margin < hopeMargin) {
                iter.remove();
            }
            //TODO 此处如何分开无销量的和自己货物充足的？
            int recCnt = new BigDecimal(product.getDailyVolume() * 4).intValue() - getOrderCnt(id, myOrder) - getAssertCnt(id, rfAssert);
            if(recCnt <= 0) {
                iter.remove();
            }
            product.setRecommendCount(recCnt);
        }
    }

    private int getOrderCnt(Integer id, Map<Integer, List<EveOrder>> myOrder) {
        List<EveOrder> orderList = myOrder.get(id);
        int ret = 0;
        if(orderList != null) {
            for(EveOrder eveOrder : orderList) {
                if(eveOrder.getTypeId() == id) {
                    ret += eveOrder.getVolumeRemain();
                }
            }
        }
        return ret;
    }

    private int getAssertCnt(Integer id, List<AssertItem> rfAssert) {
        int ret = 0;
        if(rfAssert != null) {
            for(AssertItem item : rfAssert) {
                if(item.getTypeId() == id) {
                    ret += item.getQuantity();
                }
            }
        }
        return ret;
    }

    private void assemblePurchasePrice(Map<Integer, IndustryProduct> productMap, ItemsMapper itemsMapper) {
        IndustryactivitymaterialsMapper materialsMapper = getIndustryActivityMaterialsMapper();
        Iterator<Integer> iter = productMap.keySet().iterator();
        while(iter.hasNext()) {
            Integer id = iter.next();
            IndustryProduct product = productMap.get(id);
            IndustryactivitymaterialsExample example =
                    new IndustryactivitymaterialsExample();
            example.createCriteria().andActivityidEqualTo(PrjConst.BLUEPRINT_ACTIVITY_TYPE_MANUFACTURING).andTypeidEqualTo(product.getBlueprintTypeID());
            List<Industryactivitymaterials> materials = materialsMapper.selectByExample(example);
            double appraise = getAppraise(materials, itemsMapper, product);
            product.setPurchasePrice(appraise);
        }
    }

    private double getAppraise(List<Industryactivitymaterials> materials, ItemsMapper itemsMapper, IndustryProduct product) {
        StringBuilder sb = new StringBuilder();
        for(Industryactivitymaterials material : materials) {
            Items item = itemsMapper.selectByPrimaryKey(material.getMaterialtypeid());
            sb.append(item.getEnName());
            sb.append(" ");
            BigDecimal realCnt = NumberUtil.round(material.getQuantity() * (1 - MATERIAL_RESEARCH) * 0.94
                    , 0, RoundingMode.UP);
            sb.append(realCnt.intValue());
            sb.append("\n");
        }
        String url = getEVEPraisalUrl(sb.toString());
        HttpResponse execute = HttpRequest.post(url).execute();
        String body = execute.body();
        JSONObject appraisal = JSON.parseObject(body).getJSONObject("appraisal");
        JSONObject totals = appraisal.getJSONObject("totals");
        return totals.getDouble("buy");
    }

    private String getEVEPraisalUrl(String avatar) {
        StringBuilder sb = new StringBuilder();
        sb.append("https://evepraisal.com/appraisal.json?market=jita&raw_textarea=");
        sb.append(avatar);
        sb.append("&persist=no");
        return sb.toString();
    }

    private void assembleStationPrice(AuthAccount rfAccount, Map<Integer, IndustryProduct> productMap) {
        Map<Integer, List<EveOrder>> orderMap =
            getRfOrder(rfAccount.getAccessToken(), PrjConst.STATION_ID_RF_WINTERCO);
        Iterator<Integer> iter = productMap.keySet().iterator();
        while(iter.hasNext()) {
            Integer id = iter.next();
            List<EveOrder> eveOrders = orderMap.get(id);
            double stationPrice = Integer.MAX_VALUE;
            if(eveOrders != null) {
                for(EveOrder order : eveOrders) {
                    if(order.isBuyOrder()) {
                        continue;
                    }
                    if(!order.getLocationId().equals(PrjConst.STATION_ID_RF_WINTERCO)) {
                        continue;
                    }
                    double price = order.getPrice();
                    if(price < stationPrice) {
                        stationPrice = price;
                    }
                }
            }
            IndustryProduct industryProduct = productMap.get(id);
            industryProduct.setStationPrice(stationPrice);
            double dailyVolume = TradeUtil.getDailyVolume(id);
            industryProduct.setDailyVolume(NumberUtil.round(dailyVolume, 0, RoundingMode.UP).intValue());
        }
    }

    private Map<Integer, IndustryProduct> getProductIDList() {
        Map<Integer, IndustryProduct> ret = new HashMap<>();
        IndustryactivityproductsMapper productsMapper =
                getIndustryActivityProductsMapper();
        IndustryactivityproductsExample example =
                new IndustryactivityproductsExample();

        List<CharBlueprint> ownBpID = getOwnBlueprint();
        List<Integer> fullIDList = getFullResearchIDList(ownBpID);
        example.createCriteria().andActivityidEqualTo(PrjConst.BLUEPRINT_ACTIVITY_TYPE_MANUFACTURING).andTypeidIn(fullIDList);
        List<Industryactivityproducts> industryActivityProducts = productsMapper.selectByExample(example);
        for(Industryactivityproducts products : industryActivityProducts) {
            Integer id = products.getProducttypeid();
            IndustryProduct product = new IndustryProduct();
            product.setTypeID(id);
            product.setBlueprintTypeID(products.getTypeid());
            product.setFlowQuantity(products.getQuantity());
            ret.put(id, product);
        }
        return ret;
    }

    public void getListNeedMaterial(String productStr, AuthAccount materialAccount) throws Exception {
        Map<String, Integer> productMap = parseProductStr(productStr);
        getListNeedMaterial(productMap, materialAccount);
    }

    private Map<String, Integer> parseProductStr(String productStr) {
        Map<String, Integer> map = new HashMap<>();
        String[] records = productStr.split("\n");
        for(String record : records) {
            String[] split = record.split("\t");
            map.put(split[0], Integer.parseInt(split[1]));
        }
        return map;
    }

    public void getListNeedMaterial(Map<String, Integer> productMap, AuthAccount materialAccount) throws Exception {
        Map<Integer, Integer> productIDMap = getProductIDMap(productMap);
        Map<Integer, Integer> typeIDCountMap = getBlueprintCountMap(productIDMap);
        Map<String, Integer> materialCountMap = getMaterialCountMap(typeIDCountMap, materialAccount);
        outNeedMaterialCount(materialCountMap);
    }

    private Map<Integer, Integer> getAssertMap(List<AssertItem> anAssert) {
        Map<Integer, Integer> map = new HashMap<>();
        for(AssertItem item : anAssert) {
            Integer integer = map.get(item.getTypeId());
            if(integer == null) {
                integer = 0;
            }
            integer += item.getQuantity();
            map.put(item.getTypeId(), integer);
        }
        return map;
    }


    private void outNeedMaterialCount(Map<String, Integer> materialCountMap) throws IOException {
        File file = new File("result/industry/needMaterialCount");
        FileWriter writer = new FileWriter(file);
        Iterator<String> iter = materialCountMap.keySet().iterator();
        while (iter.hasNext()) {
            String name = iter.next();
            Integer count = materialCountMap.get(name);
            StringBuilder sb = new StringBuilder();
            sb.append(name);
            sb.append(" ");
            sb.append(count);
            writer.write(sb.toString());
            writer.write("\r\n");
        }
        writer.flush();
        writer.close();
    }

    private Map<String, Integer> getMaterialCountMap(Map<Integer, Integer> typeIDCountMap, AuthAccount materialAccount) throws Exception {
        List<AssertItem> anAssert = getAssert(materialAccount);
        Map<Integer, Integer> assertMap = getAssertMap(anAssert);
        Map<Integer, Integer> materialTypeMap = new HashMap<>();
        IndustryactivitymaterialsMapper materialsMapper = getIndustryActivityMaterialsMapper();
        Iterator<Integer> iter = typeIDCountMap.keySet().iterator();
        while (iter.hasNext()) {
            Integer id = iter.next();
            Integer blueCnt = typeIDCountMap.get(id);
            IndustryactivitymaterialsExample example = new IndustryactivitymaterialsExample();
            example.createCriteria().andTypeidEqualTo(id).andActivityidEqualTo(PrjConst.BLUEPRINT_ACTIVITY_TYPE_MANUFACTURING);
            List<Industryactivitymaterials> materialsList = materialsMapper.selectByExample(example);
            for(int i = 0; i < blueCnt; i++) {
                for (Industryactivitymaterials materials : materialsList) {
                    Integer count = materialTypeMap.get(materials.getMaterialtypeid());
                    if(count == null) {
                        count = 0;
                    }
                    count += materials.getQuantity();
                    materialTypeMap.put(materials.getMaterialtypeid(), count);
                }
            }
        }
        ItemsMapper itemsMapper = getItemsMapper();
        Map<String, Integer> ret = new HashMap<>();
        Iterator<Integer> iterator = materialTypeMap.keySet().iterator();
        while (iterator.hasNext()) {
            Integer id = iterator.next();
            Integer count = materialTypeMap.get(id);
            Items items = itemsMapper.selectByPrimaryKey(id);
            BigDecimal mul = NumberUtil.mul(new BigDecimal(count), new BigDecimal(1.0-MATERIAL_RESEARCH));
            //TODO 6%建筑插 以后改为传入
            mul = NumberUtil.mul(mul, new BigDecimal(0.94));
            Integer ownCnt = assertMap.get(id);
            if(ownCnt != null) {
                mul = NumberUtil.sub(mul, ownCnt);
            }
            int needCount = NumberUtil.round(mul, 0, RoundingMode.UP).intValue();
            if(needCount > 0) {
                ret.put(items.getEnName(), needCount);
            }
        }
        return ret;
    }

    private Map<Integer, Integer> getBlueprintCountMap(Map<Integer, Integer> productIDMap) {
        Map<Integer, Integer> typeIDCountMap = new HashMap<>();
        IndustryactivityproductsMapper productsMapper = getIndustryActivityProductsMapper();
        Iterator<Integer> iter = productIDMap.keySet().iterator();
        while(iter.hasNext()) {
            Integer id = iter.next();
            IndustryactivityproductsExample example = new IndustryactivityproductsExample();
            example.createCriteria().andActivityidEqualTo(PrjConst.BLUEPRINT_ACTIVITY_TYPE_MANUFACTURING).andProducttypeidEqualTo(id);
            Industryactivityproducts product = productsMapper.selectByExample(example).get(0);
            Integer productCnt = productIDMap.get(id);
            BigDecimal blueprintCnt = NumberUtil.div(new BigDecimal(productCnt), new BigDecimal(product.getQuantity()), 0, RoundingMode.UP);
            typeIDCountMap.put(product.getTypeid(), blueprintCnt.intValue());
        }
        return typeIDCountMap;
    }

    private Map<Integer, Integer> getProductIDMap(Map<String, Integer> productMap) throws Exception {
        Map<Integer, Integer> idMap = new HashMap<>();
        ItemsMapper itemsMapper = getItemsMapper();
        ItemsExample example = new ItemsExample();
        example.createCriteria().andEnNameIn(new ArrayList<>(productMap.keySet()));
        List<Items> items = itemsMapper.selectByExample(example);
        for(Items item : items) {
            Integer count = productMap.get(item.getEnName());
            idMap.put(item.getId(), count);
        }
        return idMap;
    }
}
