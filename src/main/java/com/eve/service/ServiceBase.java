package com.eve.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.eve.dao.*;
import com.eve.entity.AssertItem;
import com.eve.entity.AuthAccount;
import com.eve.entity.CharBlueprint;
import com.eve.entity.EveOrder;
import com.eve.entity.database.*;
import com.eve.util.PrjConst;
import com.eve.util.TradeUtil;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ServiceBase {
    private static SqlSession sqlSession;

    protected HttpResponse sendGetRequest(String url, Map<String, Object> paramMap) {
        return TradeUtil.sendGetRequest(url, paramMap);
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

    protected List<Integer> getCanManuTypeID(List<Integer> fullIDList) {
        IndustryactivityproductsMapper productMapper = getIndustryActivityProductsMapper();
        IndustryactivityproductsExample example = new IndustryactivityproductsExample();
        example.createCriteria().andTypeidIn(fullIDList).andActivityidEqualTo(PrjConst.BLUEPRINT_ACTIVITY_TYPE_MANUFACTURING);
        List<Industryactivityproducts> industryActivityProductsList = productMapper.selectByExample(example);
        List<Integer> ret = new ArrayList<>();
        for(Industryactivityproducts products : industryActivityProductsList) {
            ret.add(products.getProducttypeid());
        }
        return ret;
    }

    protected List<CharBlueprint> getOwnBlueprint() {
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

    protected List<Integer> getFullResearchIDList(List<CharBlueprint> ownBpID) {
        List<Integer> ret = new ArrayList<>();
        for(CharBlueprint bp : ownBpID) {
            if(bp.getTimeEfficiency() >= 20 && bp.getMaterialEfficiency() >= 10) {
                ret.add(bp.getTypeId());
            }
        }
        return ret;
    }

    protected List<AssertItem> getAssert(AuthAccount account) throws Exception {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("datasource", PrjConst.DATASOURCE);
        paramMap.put("page", 1);
        paramMap.put("token", account.getAccessToken());
        HttpResponse httpResponse = sendGetRequest(replaceBraces(PrjConst.CHAR_ASSERT_URL,
                account.getId()), paramMap);
        String body = httpResponse.body();
        return JSON.parseArray(body, AssertItem.class);
    }

    protected List<AssertItem> filterRFAssert(List<AssertItem> anAssert, List<Integer> exclude, String locationID) {
        List<AssertItem> ret = new ArrayList<>();
        for(AssertItem item : anAssert) {
            if(Long.parseLong(locationID) == item.getLocationId() && !exclude.contains(item.getTypeId())) {
                ret.add(item);
            }
        }
        return ret;
    }

    protected Map<Integer, List<EveOrder>> getMyOrder(AuthAccount account) {
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

    protected ItemsMapper getItemsMapper() {
        return sqlSession.getMapper(ItemsMapper.class);
    }

    protected IndustryactivitymaterialsMapper getIndustryActivityMaterialsMapper() {
        return sqlSession.getMapper(IndustryactivitymaterialsMapper.class);
    }

    protected IndustryactivityproductsMapper getIndustryActivityProductsMapper() {
        return sqlSession.getMapper(IndustryactivityproductsMapper.class);
    }

    protected InvmarketgroupsMapper getMarketgroupsMapper() {
        return sqlSession.getMapper(InvmarketgroupsMapper.class);
    }
}
