package com.eve.entity;

import cn.hutool.core.codec.Base64;
import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.eve.util.PrjConst;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class AuthAccount {
    private String id;
    private String name;
    private String refreshToken;
    private String accessToken;

    public AuthAccount(String id, String name, String refreshToken) {
        this.id = id;
        this.name = name;
        this.refreshToken = refreshToken;
        this.accessToken = getAccessTokenByEsi();
    }

    private String getAccessTokenByEsi() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("grant_type", "refresh_token");
        paramMap.put("refresh_token", refreshToken);
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
}
