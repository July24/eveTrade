package com.eve.util;

import java.util.Scanner;

public class PrjConst {
    //过滤低流量物品


    public static final String TOKEN_URL = "https://login.eveonline.com/oauth/token";
    public static final String ORDER_URL = "https://esi.evetech.net/latest/markets/{region_id}/orders/";
    public static final String ORDER_HISTORY_URL = "https://esi.evetech.net/latest/markets/{region_id}/history/";
    public static final String CHAR_ORDER_URL = "https://esi.evetech.net/latest/characters/{character_id}/orders/";
    public static final String CHAR_ASSERT_URL = "https://esi.evetech.net/latest/characters/{character_id}/assets/";
    public static final String LIST_STRUCTURE_ORDER_URL = "https://esi.evetech.net/latest/markets/structures/{structure_id}/";


    public static final String DATASOURCE = "tranquility";
    public static final String TOKEN_TYPE = "Bearer";

    public static final String ALLEN_CHAR_ID = "2116759059";
    public static final String ALLEN_CHAR_NAME = "Allen dy";
    public static final String ALLEN_REFRESH_TOKEN = "JmRjraX_KcdLYTHM0oZiKqxQEj2XpbfbghwLuJicvwZmoPRCpa_8E_Dadj9ht0LfOdh_4lvaXHBbRZweEpV_APt4q3cH9A1kXdSgTZB8V-HOy9LUVPtKbN0BmEtJd5Lnc6xJYkc99UQyW0ZbF9oyjQdfBw2jhB4qNC5sWaYW5RWpV8xCcY6aFiRA6Bbnh803jd7KFJjk4NKvRwNH993JN6c28rrtHiMA0coBs14VVJJ3Zh9A-CuaOLO5TPIjaH-_I3pr66SHF4Ilo0Qf8_uJWILwgBlaBnBEO2Gu5r5eL2iXTCNP8EoVr3vOUQ3N9GiesHrQ8Kw_t-PGtuL4bl2GAgjXTVjb8x9T1bgJusUsDwJi1l_pwT4JVIv9I760Zsx810rUEn5pqEvyqNQ5nSGxHKsCqq-h9W1RyC_ZbRIXFhs";

    public static final String LEAH_CHAR_ID = "2116774705";
    public static final String LEAH_CHAR_NAME = "Leah dy";
    public static final String LEAH_REFRESH_TOKEN = "nDvp6BI-k6UaTQjlgm-RuekEKsKeiytwg_fh6DkqRX3gEpGbNIAitaeqZY660zNpsaB5G2wH4MD6xMynsqeW61WCR64I-RF09Di1fz3N9nYtemJPQfyJL4M-i3FNnUkRQ_WRCQ3-N6MN6cVHc7lYFKKBzJKDVhFiy79Z7Zh-4oEcptohNKfPd-mZsl8bs7uzdiIqOCfk5UOlpG8W9yqRYp4IF4GFn_jU4x-LB2wznWtiBOd0A3XxF4bPE7NC46wg87DZ6zfNbseYmD1An89spzUUS8pVN-JmgEW3RmbSZQvrHUl_JeeFaqYGEfgV23cfqgDZGQr5ipjr45UhH7ycyEfS-UhngpuS_Sfv9Xg-G-tFZ2Rftvsn_H35zxAfN1y-TD1tha22c8txtLW_C5JFnY59sWyiaqFEKYZYUoyxsQM";

    public static final String SANJI_REFRESH_TOKEN = "f8yeJU-n_kA3lDY" +
            "-WwaCIuaKghcu0DkH_h_-BiM1mnSzOVvotLdXoTo5XjdcyLhLpgWHWyEThTy-stqvnnQb4q2RBX09TAtPCB74olD45BUAvdBXl7DsV_zOpnaWdQSE8akTc3LLm1Gh9iZmb2FNpnazdVFXR5vnhVAFAonWXBMCtcfYyniwrnIQh2oVzktF27H5qj7IkGtaiNITsbj1j2u5HFDXbATNLSlK9U8XXnkP5dUAyBJe941I9r6ZxTJQv8Z_SNNb8NzQ4ed4GZTV8q9FF9jYNBR81FiHGucWTTVr3H2zmj6zo9ggk87RmtadTd-0BkGBICS6gMHR2Lh5LVzOFxL73_PfsNVwoZNPZRrmy7jRdm9BFGJByVxjYXslSH4dRogYOdOiJnaCpBewPgMs6ixkoro1welD0KaiJvI";

    public static final String CLINET_ID = "713aecdf1382415687f38d64a72b036b";
    public static final String SECRET_KEY =
            "fgJwX6sdQHL5YMJK6Kvdph7GV8Nv6k1OcyhCO0oa";
    public static final String SOLAR_SYSTEM_ID_JITA = "30000142";
    public static final String STATION_ID_RF_WINTERCO = "1031792377493";
    public static final String STATION_ID_JITA_NAVY4 = "60003760";
    public static final String REGION_ID_OASA = "10000040";
    public static final String REGION_ID_THE_FORGE = "10000002";
    public static final double SELL_FAX = 0.028;
    public static final double AVG_BROKER_FAX = 0.03;
    public static final int EXPRESS_FAX_CUBIC_METRES = 1400;

    public static final String PATH_MARKET_DATA_FILE_FOLDER = "result/trade/marketData";
    public static final String PATH_JITA_INVENTORY = "result/trade/inventory/jitaInventory";
    public static final String PATH_RF_INVENTORY = "result/trade/inventory/rfInventory";
    public static final String PATH_RECOMMEND_NOT_BUY = "result/trade/recommendNotBuyList";
    public static final String PATH_RECOMMEND_BUY_SIMPLE = "result/trade/simple";

    public static final String SEPARATOR_PREFIX = "----";

    public static final int BLUEPRINT_ACTIVITY_TYPE_MANUFACTURING = 1;
    public static final int BLUEPRINT_ACTIVITY_TYPE_COPYING = 5;
    public static final int BLUEPRINT_ACTIVITY_TYPE_RESEARCH_MATERIAL = 3;
    public static final int BLUEPRINT_ACTIVITY_TYPE_RESEARCH_TIME = 4;
    public static final int BLUEPRINT_ACTIVITY_TYPE_INVENTION = 8;
}
