package com.eve.service;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.eve.dao.IndustryActivityMaterialsMapper;
import com.eve.dao.IndustryActivityProductsMapper;
import com.eve.dao.ItemsMapper;
import com.eve.entity.AuthAccount;
import com.eve.entity.EveOrder;
import com.eve.entity.IndustryProduct;
import com.eve.entity.database.*;
import com.eve.util.PrjConst;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class IndustryService extends ServiceBase {
    private static double MATERIAL_RESEARCH = 0.1;

    public static void main(String[] args) throws Exception {
        IndustryService is = new IndustryService();
        Map<String, Integer> productMap = new HashMap<>();
        productMap.put("Phased Plasma M",5000);
        productMap.put("Fusion M",5000);
//        productMap.put("425mm AutoCannon I",10);
//        is.getListNeedMaterial(productMap);
        AuthAccount account = new AuthAccount(PrjConst.ALLEN_CHAR_ID, PrjConst.ALLEN_CHAR_NAME, PrjConst.ALLEN_REFRESH_TOKEN);
        is.getManufacturingList(account, 0.3);
    }

    public void getManufacturingList(AuthAccount rfAccount,
                                     double hopeMargin) throws Exception {
        //TODO 减去库存
        ItemsMapper itemsMapper = getItemsMapper();
        Map<Integer, IndustryProduct> productMap = getProductIDList();
        assembleStationPrice(rfAccount, productMap);
        assemblePurchasePrice(productMap, itemsMapper);
        filterMargin(productMap, hopeMargin);
        outRecommendedManufacturing(productMap, itemsMapper);
    }

    private void outRecommendedManufacturing(Map<Integer, IndustryProduct> productMap, ItemsMapper itemsMapper) throws Exception {
        File file = new File("result/industry/recommendManufacturing");
        FileWriter writer = new FileWriter(file);
        Iterator<Integer> iter = productMap.keySet().iterator();
        while (iter.hasNext()) {
            Integer id = iter.next();
            StringBuilder sb = new StringBuilder();
            sb.append(itemsMapper.selectByPrimaryKey(id).getEnName());
            writer.write(sb.toString());
            writer.write("\r\n");
        }
        writer.flush();
        writer.close();
    }

    private void filterMargin(Map<Integer, IndustryProduct> productMap, double hopeMargin) {
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
        }
    }

    private void assemblePurchasePrice(Map<Integer, IndustryProduct> productMap, ItemsMapper itemsMapper) {
        IndustryActivityMaterialsMapper materialsMapper = getIndustryActivityMaterialsMapper();
        Iterator<Integer> iter = productMap.keySet().iterator();
        while(iter.hasNext()) {
            Integer id = iter.next();
            IndustryProduct product = productMap.get(id);
            IndustryActivityMaterialsExample example =
                    new IndustryActivityMaterialsExample();
            example.createCriteria().andActivityidEqualTo(PrjConst.BLUEPRINT_ACTIVITY_TYPE_MANUFACTURING).andBlueprinttypeidEqualTo(product.getBlueprintTypeID());
            List<IndustryActivityMaterials> materials = materialsMapper.selectByExample(example);
            double appraise = getAppraise(materials, itemsMapper, product);
            product.setPurchasePrice(appraise);
        }
    }

    private double getAppraise(List<IndustryActivityMaterials> materials, ItemsMapper itemsMapper, IndustryProduct product) {
        StringBuilder sb = new StringBuilder();
        for(IndustryActivityMaterials material : materials) {
            Items item = itemsMapper.selectByPrimaryKey(material.getMaterialtypeid());
            sb.append(item.getEnName());
            sb.append(" ");
            BigDecimal realCnt = NumberUtil.round(material.getQuantity() * (1 - MATERIAL_RESEARCH)
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
            for(EveOrder order : eveOrders) {
                if(order.isBuyOrder()) {
                    continue;
                }
                if(!order.getLocationId().equals(PrjConst.STATION_ID_RF_WINTERCO)) {
                    continue;
                }
                double price = Double.parseDouble(order.getPrice());
                if(price < stationPrice) {
                    stationPrice = price;
                }
            }
            productMap.get(id).setStationPrice(stationPrice);
        }
    }

    private Map<Integer, IndustryProduct> getProductIDList() {
        Map<Integer, IndustryProduct> ret = new HashMap<>();
        IndustryActivityProductsMapper productsMapper =
                getIndustryActivityProductsMapper();
        IndustryActivityProductsExample example =
                new IndustryActivityProductsExample();
        example.createCriteria().andActivityidEqualTo(PrjConst.BLUEPRINT_ACTIVITY_TYPE_MANUFACTURING).andBlueprinttypeidIn(new ArrayList<>(Arrays.asList(PrjConst.OWN_BLUEPRINT)));
        List<IndustryActivityProducts> industryActivityProducts = productsMapper.selectByExample(example);
        for(IndustryActivityProducts products : industryActivityProducts) {
            Integer id = products.getProducttypeid();
            IndustryProduct product = new IndustryProduct();
            product.setTypeID(id);
            product.setBlueprintTypeID(products.getBlueprinttypeid());
            product.setFlowQuantity(products.getQuantity());
            ret.put(id, product);
        }
        return ret;
    }

    public void getListNeedMaterial(Map<String, Integer> productMap) throws Exception {
        Map<Integer, Integer> productIDMap = getProductIDMap(productMap);
        Map<Integer, Integer> typeIDCountMap = getBlueprintCountMap(productIDMap);
        Map<String, Integer> materialCountMap = getMaterialCountMap(typeIDCountMap);
        outNeedMaterialCount(materialCountMap);
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

    private Map<String, Integer> getMaterialCountMap(Map<Integer, Integer> typeIDCountMap) {
        Map<Integer, Integer> materialTypeMap = new HashMap<>();
        IndustryActivityMaterialsMapper materialsMapper = getIndustryActivityMaterialsMapper();
        Iterator<Integer> iter = typeIDCountMap.keySet().iterator();
        while (iter.hasNext()) {
            Integer id = iter.next();
            Integer blueCnt = typeIDCountMap.get(id);
            IndustryActivityMaterialsExample example = new IndustryActivityMaterialsExample();
            example.createCriteria().andBlueprinttypeidEqualTo(id).andActivityidEqualTo(PrjConst.BLUEPRINT_ACTIVITY_TYPE_MANUFACTURING);
            List<IndustryActivityMaterials> materialsList = materialsMapper.selectByExample(example);
            for(int i = 0; i < blueCnt; i++) {
                for (IndustryActivityMaterials materials : materialsList) {
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
            ret.put(items.getEnName(), NumberUtil.round(mul, 0 ,RoundingMode.UP).intValue());
        }
        return ret;
    }

    private Map<Integer, Integer> getBlueprintCountMap(Map<Integer, Integer> productIDMap) {
        Map<Integer, Integer> typeIDCountMap = new HashMap<>();
        IndustryActivityProductsMapper productsMapper = getIndustryActivityProductsMapper();
        Iterator<Integer> iter = productIDMap.keySet().iterator();
        while(iter.hasNext()) {
            Integer id = iter.next();
            IndustryActivityProductsExample example = new IndustryActivityProductsExample();
            example.createCriteria().andActivityidEqualTo(PrjConst.BLUEPRINT_ACTIVITY_TYPE_MANUFACTURING).andProducttypeidEqualTo(id);
            IndustryActivityProducts product = productsMapper.selectByExample(example).get(0);
            Integer productCnt = productIDMap.get(id);
            BigDecimal blueprintCnt = NumberUtil.div(new BigDecimal(productCnt), new BigDecimal(product.getQuantity()), 0, RoundingMode.UP);
            typeIDCountMap.put(product.getBlueprinttypeid(), blueprintCnt.intValue());
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
