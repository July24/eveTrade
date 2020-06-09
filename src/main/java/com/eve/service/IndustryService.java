package com.eve.service;

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
import io.swagger.models.auth.In;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class IndustryService extends ServiceBase {
    private static double MATERIAL_RESEARCH = 0.1;
    private static double T2_MATERIAL_RESEARCH = 0.01;

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
//        String productStr = "Medium Core Defense Field Extender II\t116\n" +
//                 "Medium Capacitor Control Circuit II\t113\n" +
//                "Small Core Defense Field Extender II\t60\n" +
//                "Small Hyperspatial Velocity Optimizer II\t60\n" +
//                "Medium Anti-EM Screen Reinforcer II\t56";
//        is.getListNeedMaterial(productStr, sanjiAccount);
//        is.getManufacturingList(account, 0.1, true);
//        is.getBlueprintBuyList();


        //---------发明流程-----------
        String t2ProductStr = "Medium Core Defense Field Extender II\t1\n" +
                "Medium Capacitor Control Circuit II\t1\n" +
                "Small Core Defense Field Extender II\t1\n" +
                "Small Hyperspatial Velocity Optimizer II\t1\n" +
                "Medium Anti-EM Screen Reinforcer II\t1";
//        is.getT2CopyCount(t2ProductStr);
//        String t1BPCProductStr = "Medium Anti-EM Screen Reinforcer I Blueprint\t24\n" +
//                "Small Hyperspatial Velocity Optimizer I Blueprint\t24\n" +
//                "Medium Capacitor Control Circuit I Blueprint\t48\n" +
//                "Small Core Defense Field Extender I Blueprint\t24\n" +
//                "Medium Core Defense Field Extender I Blueprint\t48";
//        is.t2InventionMaterial(t1BPCProductStr);
//        is.getListNeedMaterial(t2ProductStr, sanjiAccount);
    }

    private void t2InventionMaterial(String productStr) throws Exception {
        Map<String, Integer> productMap = parseProductStr(productStr);
        Map<Integer, Integer> productIDMap = getProductIDMap(productMap);
        Map<Integer, Integer> materialMap = getInventionMaterial(productIDMap);
        Map<Integer, Items> itemMap = getItemMap(getItemsMapper(), new ArrayList<>(materialMap.keySet()));
        outInventionMaterial(materialMap, itemMap);
    }

    private void outInventionMaterial(Map<Integer, Integer> productMap, Map<Integer, Items> itemMap) throws IOException {
        File file = new File("result/industry/inventionMaterial");
        FileWriter writer = new FileWriter(file);
        Iterator<Integer> iter = productMap.keySet().iterator();
        while (iter.hasNext()) {
            Integer id = iter.next();
            Integer count = productMap.get(id);
            StringBuilder sb = new StringBuilder();
            Items items = itemMap.get(id);
            sb.append(items.getEnName());
            sb.append("\t");
            sb.append(count);
            writer.write(sb.toString());
            writer.write("\r\n");
        }
        writer.flush();
        writer.close();
    }

    private Map<Integer, Integer> getInventionMaterial(Map<Integer, Integer> productIDMap) {
        Map<Integer, Integer> map = new HashMap<>();
        IndustryactivitymaterialsMapper materialsMapper = getIndustryActivityMaterialsMapper();
        Iterator<Integer> iter = productIDMap.keySet().iterator();
        int decryptorCnt = 0;
        while (iter.hasNext()) {
            Integer id = iter.next();
            Integer inventionCnt = productIDMap.get(id);
            IndustryactivitymaterialsExample example = new IndustryactivitymaterialsExample();
            example.createCriteria().andTypeidEqualTo(id).andActivityidEqualTo(PrjConst.BLUEPRINT_ACTIVITY_TYPE_INVENTION);
            List<Industryactivitymaterials> materials = materialsMapper.selectByExample(example);
            for(Industryactivitymaterials material : materials) {
                Integer materialID = material.getMaterialtypeid();
                Integer quantity = material.getQuantity();
                Integer count = map.get(materialID);
                if(count == null) {
                    count = 0;
                }
                count += (quantity * inventionCnt);
                map.put(materialID, count);
            }
            decryptorCnt += inventionCnt;
        }
        map.put(PrjConst.DECRYPTOR_AUGMENTATION_ID, decryptorCnt);
        return map;
    }

    private void getT2CopyCount(String productStr) throws Exception {
        Map<String, Integer> productMap = parseProductStr(productStr);

        Map<Integer, Integer> productIDMap = getProductIDMap(productMap);
        Map<Integer, Integer> t1BPMap = getT1BPMap(productIDMap);
        Map<Integer, Items> itemMap = getItemMap(getItemsMapper(), new ArrayList<>(t1BPMap.keySet()));
        outCopyCount(t1BPMap, itemMap);
    }

    private Map<Integer, Integer> getT1BPMap(Map<Integer, Integer> productIDMap) {
        IndustryactivityproductsMapper productsMapper = getIndustryActivityProductsMapper();
        IndustryactivityproductsExample example = new IndustryactivityproductsExample();
        example.createCriteria().andProducttypeidIn(new ArrayList<>(productIDMap.keySet()));
        List<Industryactivityproducts> industryactivityproducts = productsMapper.selectByExample(example);
        Map<Integer, Integer> t2BP = new HashMap<>();
        for(Industryactivityproducts product : industryactivityproducts) {
            t2BP.put(product.getTypeid(), productIDMap.get(product.getProducttypeid()));
        }
        IndustryactivityproductsExample another = new IndustryactivityproductsExample();
        another.createCriteria().andProducttypeidIn(new ArrayList<>(t2BP.keySet())).andActivityidEqualTo(PrjConst.BLUEPRINT_ACTIVITY_TYPE_INVENTION);
        List<Industryactivityproducts> inventionProducts = productsMapper.selectByExample(another);
        Map<Integer, Integer> map = new HashMap<>();
        for(Industryactivityproducts product : inventionProducts) {

            map.put(product.getTypeid(), t2BP.get(product.getProducttypeid()));
        }
        return map;
    }

    private void outCopyCount(Map<Integer, Integer> productMap, Map<Integer, Items> itemMap) throws IOException {
        File file = new File("result/industry/copyCount");
        BigDecimal avgSuc = BigDecimal.valueOf(NumberUtil.div(1, PrjConst.INVENTION_RIG_SUCCESS_AUGMENTATION_L4));
        avgSuc = NumberUtil.round(avgSuc, 0, RoundingMode.UP);

        FileWriter writer = new FileWriter(file);
        Iterator<Integer> iter = productMap.keySet().iterator();
        while (iter.hasNext()) {
            Integer bpID = iter.next();
            Integer count = productMap.get(bpID);
            BigDecimal cnt = new BigDecimal(count).divide(new BigDecimal(1 + DECRYPTOR.Augmentation.Runs));
            cnt = NumberUtil.round(cnt, 0, RoundingMode.UP);
            BigDecimal copyCnt = avgSuc.multiply(cnt);
            StringBuilder sb = new StringBuilder();
            Items items = itemMap.get(bpID);
            sb.append(items.getEnName());
            sb.append("\t");
            sb.append(copyCnt.intValue());
            writer.write(sb.toString());
            writer.write("\r\n");
        }
        writer.flush();
        writer.close();
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

    public void rigManufacturingExport(double hopeMargin, boolean onT2Manu, double materialDiscount) throws Exception {
        //TODO 出口船插
        ItemsMapper itemsMapper = getItemsMapper();
        Map<Integer, IndustryProduct> productMap = getProductIDList(itemsMapper, onT2Manu);

//        assembleRFSellPrice(rfAccount, productMap);
//        assembleMaterialPurchasePrice(productMap, itemsMapper);
//        filterMargin(productMap, hopeMargin, myOrder, anAssert);
//        outRecommendedManufacturing(productMap, itemsMapper);
    }

    public void getManufacturingList(AuthAccount rfAccount,
                                     double hopeMargin, boolean onT2Manu) throws Exception {
        Map<Integer, List<EveOrder>> myOrder = getMyOrder(rfAccount);
        List<AssertItem> anAssert = getAssert(rfAccount);
        ItemsMapper itemsMapper = getItemsMapper();
        Map<Integer, IndustryProduct> productMap = getProductIDList(itemsMapper, onT2Manu);
        assembleRFSellPrice(rfAccount, productMap);
        assembleMaterialPurchasePrice(productMap, itemsMapper);
        filterMargin(productMap, hopeMargin, myOrder, anAssert);
        outRecommendedManufacturing(productMap, itemsMapper);
    }

    private void outRecommendedManufacturing(Map<Integer, IndustryProduct> productMap, ItemsMapper itemsMapper) throws Exception {
        List<Map.Entry<Integer,IndustryProduct>> list = new ArrayList<>(productMap.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Integer, IndustryProduct>>() {
            @Override
            public int compare(Map.Entry<Integer, IndustryProduct> o1, Map.Entry<Integer, IndustryProduct> o2) {
                return o2.getValue().getRecommendCount() - o1.getValue().getRecommendCount();
            }
        });


        File file = new File("result/industry/recommendManufacturing");
        FileWriter writer = new FileWriter(file);
        Map<Integer, Items> map = getItemMap(itemsMapper, new ArrayList<>(productMap.keySet()));
        List<IndustryProduct> t2 = new ArrayList<>();
        for (Map.Entry<Integer,IndustryProduct> entry : list) {
            Integer id = entry.getKey();
            IndustryProduct industryProduct = entry.getValue();
            if(industryProduct.isT2()) {
                int remainRun = industryProduct.getRemainRun();
                if(remainRun > 0) {
                    writer.write(map.get(id).getEnName());
                    writer.write("\t");
                    writer.write(String.valueOf(remainRun));
                    writer.write("\r\n");
                    int recCount = industryProduct.getRecommendCount();
                    recCount -= remainRun;
                    if(recCount > 0) {
                        industryProduct.setRecommendCount(recCount);
                        t2.add(industryProduct);
                    }
                } else {
                    t2.add(industryProduct);
                }
            } else {
                writer.write(map.get(id).getEnName());
                writer.write("\t");
                writer.write(String.valueOf(industryProduct.getRecommendCount()));
                writer.write("\r\n");
            }
        }
        writer.write("==============T2==============");
        writer.write("\r\n");
        for(IndustryProduct product : t2) {
            writer.write(map.get(product.getTypeID()).getEnName());
            writer.write("\t");
            writer.write(String.valueOf(product.getRecommendCount()));
            writer.write("\r\n");
        }
        writer.flush();
        writer.close();
    }

    private void filterMargin(Map<Integer, IndustryProduct> productMap, double hopeMargin, Map<Integer,
            List<EveOrder>> myOrder, List<AssertItem> rfAssert) {
        HashMap<Integer, Integer> expressMap = getExpressMap();
        Iterator<Integer> iter = productMap.keySet().iterator();
        while(iter.hasNext()) {
            Integer id = iter.next();
            IndustryProduct product = productMap.get(id);
            double flowStationPrice = product.getStationPrice() * product.getFlowQuantity();
            double purchasePrice = product.getPurchasePrice();
            if(product.isT2()) {
                flowStationPrice -= product.getRunInventoryFee();
            }
            double margin = (flowStationPrice - purchasePrice)/purchasePrice;
            if(margin < hopeMargin) {
                iter.remove();
            }
            Integer expCnt = expressMap.get(id) != null ? expressMap.get(id) : 0;
            //TODO 此处如何分开无销量的和自己货物充足的？
            int recCnt =
                    new BigDecimal(product.getDailyVolume() * 4).intValue() - getOrderCnt(id, myOrder) - getAssertCnt(id, rfAssert) - expCnt;
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

    private void assembleMaterialPurchasePrice(Map<Integer, IndustryProduct> productMap, ItemsMapper itemsMapper) {
        IndustryactivitymaterialsMapper materialsMapper = getIndustryActivityMaterialsMapper();
        Iterator<Integer> iter = productMap.keySet().iterator();
        while(iter.hasNext()) {
            Integer id = iter.next();
            IndustryProduct product = productMap.get(id);
            IndustryactivitymaterialsExample example =
                    new IndustryactivitymaterialsExample();
            example.createCriteria().andActivityidEqualTo(PrjConst.BLUEPRINT_ACTIVITY_TYPE_MANUFACTURING).andTypeidEqualTo(product.getBlueprintTypeID());
            List<Industryactivitymaterials> materials = materialsMapper.selectByExample(example);
            double appraise = getAppraise(materials, itemsMapper);
            product.setPurchasePrice(appraise);
        }
    }

    private double getAppraise(Map<Integer, Integer> itemMap, ItemsMapper itemsMapper, boolean buy) {
        StringBuilder sb = new StringBuilder();
        Map<Integer, Items> map = getItemMap(itemsMapper, new ArrayList<>(itemMap.keySet()));
        Iterator<Integer> iter = itemMap.keySet().iterator();
        while(iter.hasNext()) {
            Integer next = iter.next();
            Items item = map.get(next);
            sb.append(item.getEnName());
            sb.append(" ");
            sb.append(itemMap.get(next));
            sb.append("\n");
        }
        String url = getEVEPraisalUrl(sb.toString());
        HttpResponse execute = HttpRequest.post(url).execute();
        String body = execute.body();
        JSONObject appraisal = JSON.parseObject(body).getJSONObject("appraisal");
        JSONObject totals = appraisal.getJSONObject("totals");
        return buy ? totals.getDouble("buy") : totals.getDouble("sell");
    }

    private double getAppraise(List<Industryactivitymaterials> materials, ItemsMapper itemsMapper) {
        StringBuilder sb = new StringBuilder();
        Map<Integer, Items> map = getItemMap(itemsMapper, getMaterialTypeIDList(materials));

        for(Industryactivitymaterials material : materials) {
            Items item = map.get(material.getMaterialtypeid());
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

    private List<Integer> getMaterialTypeIDList(List<Industryactivitymaterials> materials) {
        List<Integer> ret = new ArrayList<>();
        for(Industryactivitymaterials material : materials) {
            ret.add(material.getMaterialtypeid());
        }
        return ret;
    }

    private String getEVEPraisalUrl(String avatar) {
        StringBuilder sb = new StringBuilder();
        sb.append("https://evepraisal.com/appraisal.json?market=jita&raw_textarea=");
        sb.append(avatar);
        sb.append("&persist=no");
        return sb.toString();
    }

    private void assembleRFSellPrice(AuthAccount rfAccount, Map<Integer, IndustryProduct> productMap) {
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

    private Map<Integer, IndustryProduct> getProductIDList(ItemsMapper itemsMapper, boolean onT2Manu) {
        Map<Integer, IndustryProduct> ret = new HashMap<>();
        IndustryactivityproductsMapper productsMapper =
                getIndustryActivityProductsMapper();
        IndustryactivityproductsExample example =
                new IndustryactivityproductsExample();

        List<CharBlueprint> ownBpID = getOwnBlueprint();
        List<Integer> allRigsMarketGroup = getAllRigsMarketGroup();
        List<Integer> fullIDList = getFullResearchIDList(ownBpID);
        List<Integer> fullRigIDList = new ArrayList<>();
        example.createCriteria().andActivityidEqualTo(PrjConst.BLUEPRINT_ACTIVITY_TYPE_MANUFACTURING).andTypeidIn(fullIDList);
        List<Industryactivityproducts> industryActivityProducts = productsMapper.selectByExample(example);
        Map<Integer, Items> map = getItemMap(itemsMapper, getProductID(industryActivityProducts));
        for(Industryactivityproducts products : industryActivityProducts) {
            Integer id = products.getProducttypeid();
            Items items = map.get(id);
            if(!allRigsMarketGroup.contains(items.getMarketgroupid())) {
                continue;
            }
            fullRigIDList.add(products.getTypeid());
            IndustryProduct product = new IndustryProduct();
            product.setTypeID(id);
            product.setBlueprintTypeID(products.getTypeid());
            product.setFlowQuantity(products.getQuantity());
            ret.put(id, product);
        }
        if(onT2Manu) {
            getT2Product(ret, fullRigIDList, productsMapper, ownBpID, itemsMapper);
        }
        return ret;
    }

    private List<Integer> getProductID(List<Industryactivityproducts> industryActivityProducts) {
        List<Integer> ret = new ArrayList<>();
        for(Industryactivityproducts product : industryActivityProducts) {
            ret.add(product.getProducttypeid());
        }
        return ret;
    }

    private void getT2Product(Map<Integer, IndustryProduct> ret, List<Integer> fullRigIDList, IndustryactivityproductsMapper productsMapper, List<CharBlueprint> ownBpID, ItemsMapper itemsMapper) {
        IndustryactivityproductsExample example =
                new IndustryactivityproductsExample();
        example.createCriteria().andActivityidEqualTo(PrjConst.BLUEPRINT_ACTIVITY_TYPE_INVENTION).andTypeidIn(fullRigIDList);
        List<Industryactivityproducts> industryactivityproducts = productsMapper.selectByExample(example);
        List<Integer> t2bpID = getT2bpID(industryactivityproducts);

        IndustryactivityproductsExample another =
                new IndustryactivityproductsExample();
        another.createCriteria().andActivityidEqualTo(PrjConst.BLUEPRINT_ACTIVITY_TYPE_MANUFACTURING).andTypeidIn(t2bpID);
        List<Industryactivityproducts> t2Products = productsMapper.selectByExample(another);
        IndustryactivitymaterialsMapper materialsMapper = getIndustryActivityMaterialsMapper();
        for(Industryactivityproducts products : t2Products) {
            Integer id = products.getProducttypeid();
            IndustryProduct product = new IndustryProduct();
            product.setTypeID(id);
            product.setT2(true);
            product.setBlueprintTypeID(products.getTypeid());
            product.setFlowQuantity(PrjConst.INVENTION_BPC_DFT_RUNS_SHIP_RIG + DECRYPTOR.Augmentation.Runs);
            product.setRemainRun(getCharBlueprintByBPID(products.getTypeid(), ownBpID));
            product.setRunInventoryFee(getRunInventoryFee(products.getTypeid(), materialsMapper, itemsMapper));
            ret.put(id, product);
        }
    }

    private int getRunInventoryFee(Integer typeid, IndustryactivitymaterialsMapper materialsMapper, ItemsMapper itemsMapper) {
        double successOne = 1 / PrjConst.INVENTION_RIG_SUCCESS_AUGMENTATION_L4;

        IndustryactivitymaterialsExample example = new IndustryactivitymaterialsExample();
        example.createCriteria().andTypeidEqualTo(typeid).andActivityidEqualTo(PrjConst.BLUEPRINT_ACTIVITY_TYPE_INVENTION);
        List<Industryactivitymaterials> industryactivitymaterials = materialsMapper.selectByExample(example);
        Map<Integer, Integer> material = new HashMap<>();
        material.put(34203, 1);
        for(Industryactivitymaterials inventoryMaterial : industryactivitymaterials) {
            material.put(inventoryMaterial.getMaterialtypeid(), inventoryMaterial.getQuantity());
        }
        double appraise = getAppraise(material, itemsMapper, true);
        BigDecimal divide = (new BigDecimal(appraise).multiply(new BigDecimal(successOne))).divide(new BigDecimal(10));
        return NumberUtil.round(divide, 0, RoundingMode.UP).intValue();
    }

    private List<Integer> getT2bpID(List<Industryactivityproducts> industryactivityproducts) {
        List<Integer> ret = new ArrayList<>();
        for(Industryactivityproducts product : industryactivityproducts) {
            ret.add(product.getProducttypeid());
        }
        return ret;
    }

    private int getCharBlueprintByBPID(int blueprintID, List<CharBlueprint> ownBpID) {
        int i = 0;
        for(CharBlueprint charBlueprint : ownBpID) {
            if(charBlueprint.getTypeId() == blueprintID) {
                i += charBlueprint.getRuns();
            }
        }
        return i;
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
        Map<Integer, Items> itemMap = getItemMap(getItemsMapper(), new ArrayList<>(productIDMap.keySet()));
        Map<Industryactivityproducts, Integer> typeIDCountMap = getBlueprintCountMap(productIDMap);
        Map<String, Integer> materialCountMap = getMaterialCountMap(typeIDCountMap, materialAccount, itemMap);
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

    private boolean judgeT2(Items items) {
        Integer metagroupid = items.getMetagroupid();
        if(metagroupid == null) {
            return false;
        }
        return metagroupid == 2;
    }

    private Map<String, Integer> getMaterialCountMap(Map<Industryactivityproducts, Integer> typeIDCountMap,
                                                     AuthAccount materialAccount, Map<Integer, Items> itemMap) throws Exception {
        List<AssertItem> anAssert = getAssert(materialAccount);
        Map<Integer, Integer> assertMap = getAssertMap(anAssert);
        HashMap<Integer, Integer> expressMap = getExpressMap();

        List<ProductMaterial> productMaterials = new ArrayList<>();
        IndustryactivitymaterialsMapper materialsMapper = getIndustryActivityMaterialsMapper();
        Iterator<Industryactivityproducts> iter = typeIDCountMap.keySet().iterator();
        while (iter.hasNext()) {
            Industryactivityproducts next = iter.next();
            Items items = itemMap.get(next.getProducttypeid());
            Integer id = next.getTypeid();
            Integer blueCnt = typeIDCountMap.get(next);
            IndustryactivitymaterialsExample example = new IndustryactivitymaterialsExample();
            example.createCriteria().andTypeidEqualTo(id).andActivityidEqualTo(PrjConst.BLUEPRINT_ACTIVITY_TYPE_MANUFACTURING);
            List<Industryactivitymaterials> materialsList = materialsMapper.selectByExample(example);
            ProductMaterial productMaterial = new ProductMaterial();
            for (Industryactivitymaterials materials : materialsList) {
                MaterialOrigin origin = new MaterialOrigin();
                origin.setId(materials.getMaterialtypeid());
                origin.setT2(judgeT2(items));
                BigDecimal me = BigDecimal.valueOf(1.0 - (origin.isT2() ? T2_MATERIAL_RESEARCH : MATERIAL_RESEARCH));
                BigDecimal mul = NumberUtil.mul(new BigDecimal(materials.getQuantity()), me);
                mul = NumberUtil.round(mul, 0, RoundingMode.UP);
                origin.setCount(mul.multiply(new BigDecimal(blueCnt)).intValue());
                productMaterial.addMaterialOrigin(origin);
            }
            productMaterials.add(productMaterial);
        }

        Map<Integer, Integer> materialMap = new HashMap<>();
        for(ProductMaterial product : productMaterials) {
            List<MaterialOrigin> materialOriginList = product.getMaterialOriginList();
            for(MaterialOrigin origin : materialOriginList) {
                BigDecimal mul = new BigDecimal(origin.getCount());
                //TODO 6%建筑插 以后改为传入
                mul = NumberUtil.mul(mul, new BigDecimal("0.94"));
                mul = NumberUtil.round(mul, 0, RoundingMode.UP);
                int id = origin.getId();
                Integer count = materialMap.get(id);
                if(count == null) {
                    count = 0;
                }
                count += mul.intValue();
                materialMap.put(id, count);
            }
        }

        ItemsMapper itemsMapper = getItemsMapper();
        Map<String, Integer> ret = new HashMap<>();

        Iterator<Integer> iterator = materialMap.keySet().iterator();
        while (iterator.hasNext()) {
            Integer id = iterator.next();
            Integer count = materialMap.get(id);
            Items items = itemsMapper.selectByPrimaryKey(id);

            Integer ownCnt = assertMap.get(id);
            if(ownCnt != null) {
                count -= ownCnt;
            }
            Integer expCnt = expressMap.get(id);
            if(expCnt != null) {
                count -= expCnt;
            }
            if(count > 0) {
                ret.put(items.getEnName(), count);
            }
        }
        return ret;
    }

    private Map<Industryactivityproducts, Integer> getBlueprintCountMap(Map<Integer, Integer> productIDMap) {
        Map<Industryactivityproducts, Integer> typeIDCountMap = new HashMap<>();
        IndustryactivityproductsMapper productsMapper = getIndustryActivityProductsMapper();
        Iterator<Integer> iter = productIDMap.keySet().iterator();
        while(iter.hasNext()) {
            Integer id = iter.next();
            IndustryactivityproductsExample example = new IndustryactivityproductsExample();
            example.createCriteria().andActivityidEqualTo(PrjConst.BLUEPRINT_ACTIVITY_TYPE_MANUFACTURING).andProducttypeidEqualTo(id);
            Industryactivityproducts product = productsMapper.selectByExample(example).get(0);
            Integer productCnt = productIDMap.get(id);
            BigDecimal blueprintCnt = NumberUtil.div(new BigDecimal(productCnt),
                    new BigDecimal(product.getQuantity()), 0,
                            RoundingMode.UP);
            typeIDCountMap.put(product, blueprintCnt.intValue());
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
