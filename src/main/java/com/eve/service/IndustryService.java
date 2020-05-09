package com.eve.service;

import cn.hutool.core.util.NumberUtil;
import com.eve.dao.IndustryActivityMaterialsMapper;
import com.eve.dao.IndustryActivityProductsMapper;
import com.eve.dao.ItemsMapper;
import com.eve.entity.OrderParseResult;
import com.eve.entity.database.*;
import com.eve.util.PrjConst;
import io.swagger.models.auth.In;

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
        productMap.put("Mega Pulse Laser I",1);
        productMap.put("Miner I",10);
        productMap.put("425mm AutoCannon I",10);
        is.getListNeedMaterial(productMap);
    }

    public void getListNeedMaterial(Map<String, Integer> productMap) throws Exception {
        Map<Integer, Integer> productIDMap = getProductIDMap(productMap);
        Map<Integer, Integer> typeIDCountMap = getBlueprintCountMap(productIDMap);
        Map<String, Integer> materialCountMap = getMaterialCountMap(typeIDCountMap);
        outRecommendedSimple(materialCountMap);
    }

    private void outRecommendedSimple(Map<String, Integer> materialCountMap) throws IOException {
        File file = new File("result/industry/out");
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
