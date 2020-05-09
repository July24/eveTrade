package com.eve.service;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.eve.dao.ItemsMapper;
import com.eve.entity.EveMarketData;
import com.eve.entity.EveMarketForQuery;
import com.eve.entity.EveMarketSellOrder;
import com.eve.entity.EveOrder;
import com.eve.entity.database.Items;
import com.eve.entity.database.ItemsExample;
import com.eve.util.PrjConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QueryService {
    @Autowired
    private ItemsMapper itemsMapper;

    public List<String> likeQuery(String name) {
        ItemsExample example = new ItemsExample();
        ItemsExample.Criteria criteria = example.createCriteria();
        String q = "%" + name + "%";
        criteria.andEnNameLike(q);
        example.or().andCnNameLike(q);
        List<Items> items = itemsMapper.selectByExample(example);
        if(items.size() == 0) {
            return null;
        }
        List<String> names = new ArrayList<>();
        for(Items item : items) {
            names.add(item.getEnName());
            names.add(item.getCnName());
        }
        return names;
    }

    public List<EveOrder> queryJita(String name) {
        ItemsExample example = new ItemsExample();
        ItemsExample.Criteria criteria = example.createCriteria();
        criteria.andEnNameEqualTo(name);
        example.or().andCnNameEqualTo(name);
        List<Items> items = itemsMapper.selectByExample(example);
        if(items.size() == 0) {
            return null;
        }
        return getJitaSellOrder(items.get(0).getId());
    }

    private List<EveOrder> getJitaSellOrder(Integer id) {
        List<EveOrder> regionSellOrder = getRegionOrder(id);
        Iterator<EveOrder> iterator = regionSellOrder.iterator();
        while (iterator.hasNext()) {
            EveOrder next = iterator.next();
            if(!PrjConst.STATION_ID_JITA_NAVY4.equals(next.getLocationId())) {
                iterator.remove();
            }
        }
        return regionSellOrder;
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

    private List<EveOrder> getRegionOrder(Integer id) {
        List<EveOrder> ret = new ArrayList<>();
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("datasource", PrjConst.DATASOURCE);
        paramMap.put("page", 1);
        paramMap.put("type_id", id);
        int cnt = 0;
        while (cnt < 3) {
            HttpResponse httpResponse = sendGetRequest(replaceBraces(PrjConst.ORDER_URL,
                    PrjConst.REGION_ID_THE_FORGE), paramMap);
            int status = httpResponse.getStatus();
            if(status != 200) {
                cnt++;
                continue;
            }

            String body = httpResponse.body();
            ret.addAll(JSON.parseArray(body, EveOrder.class));
            long maxPage = Long.parseLong(httpResponse.header("x-pages"));
            for (int i = 2; i <= maxPage; i ++) {
                Map<String, Object> paramMap2 = new HashMap<>();
                paramMap2.put("datasource", PrjConst.DATASOURCE);
                paramMap2.put("page", i);
                paramMap2.put("type_id", id);
                int cnt2 = 0;
                while (cnt2 < 3) {
                    HttpResponse httpResponse2 = sendGetRequest(replaceBraces(PrjConst.ORDER_URL,
                            PrjConst.REGION_ID_THE_FORGE), paramMap2);
                    int status2 = httpResponse2.getStatus();
                    if (status2 != 200) {
                        cnt2++;
                        continue;
                    }
                    ret.addAll(JSON.parseArray(httpResponse2.body(), EveOrder.class));
                    break;
                }
            }
            break;
        }
        return ret;
    }
}
