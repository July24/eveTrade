package com.eve.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.eve.dao.*;
import com.eve.entity.EveOrder;
import com.eve.entity.database.*;
import com.eve.util.PrjConst;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServiceBase {
    private static SqlSession sqlSession;

    protected HttpResponse sendGetRequest(String url, Map<String, Object> paramMap) {
        return HttpRequest.get(url).form(paramMap).execute();
    }

    protected String replaceBraces(String url, String replace) {
        StringBuilder sb = new StringBuilder(url);
        int i1 = sb.indexOf("{");
        int i2 = sb.indexOf("}") + 1;
        StringBuilder newSb = sb.replace(i1, i2, replace);
        return newSb.toString();
    }

    protected HashMap<Integer, List<EveOrder>> getRfOrder(String accessToken,
                                                  String stationID) {
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

    public ServiceBase() {
        String mybatisConfigPath = "mybatisconfig.xml";
        InputStream inputStream = null;
        try {
            inputStream = Resources.getResourceAsStream(mybatisConfigPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
        sqlSession = sqlSessionFactory.openSession();
    }

    protected ItemsMapper getItemsMapper() {
        return sqlSession.getMapper(ItemsMapper.class);
    }

    protected IndustryActivitiesMapper getIndustryActivitiesMapper() {
        return sqlSession.getMapper(IndustryActivitiesMapper.class);
    }

    protected IndustryActivityMaterialsMapper getIndustryActivityMaterialsMapper() {
        return sqlSession.getMapper(IndustryActivityMaterialsMapper.class);
    }

    protected IndustryActivityProductsMapper getIndustryActivityProductsMapper() {
        return sqlSession.getMapper(IndustryActivityProductsMapper.class);
    }

    protected IndustryActivitySkillsMapper getIndustryActivitySkillsMapper() {
        return sqlSession.getMapper(IndustryActivitySkillsMapper.class);
    }

    protected IndustryBlueprintsMapper getIndustryBlueprintsMapper() {
        return sqlSession.getMapper(IndustryBlueprintsMapper.class);
    }
}
