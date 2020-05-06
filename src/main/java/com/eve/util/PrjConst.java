package com.eve.util;

import java.util.Scanner;

public class PrjConst {
    //过滤低流量物品
    public static final int FILTER_LOW_FLOW = 5;

    public static final String TOKEN_URL = "https://login.eveonline.com/oauth/token";
    public static final String ORDER_HISTORY_URL = "https://esi" +
            ".evetech" +
            ".net/latest/markets/{region_id}/history/";

    public static final String LIST_STRUCTURE_ORDER_URL = "https://esi" +
            ".evetech" +
            ".net/latest/markets/structures/{structure_id}/";

    public static final String DATASOURCE = "tranquility";
    public static final String TOKEN_TYPE = "Bearer";



    public static final String SANJI_REFRESH_TOKEN = "f8yeJU-n_kA3lDY" +
            "-WwaCIuaKghcu0DkH_h_-BiM1mnSzOVvotLdXoTo5XjdcyLhLpgWHWyEThTy-stqvnnQb4q2RBX09TAtPCB74olD45BUAvdBXl7DsV_zOpnaWdQSE8akTc3LLm1Gh9iZmb2FNpnazdVFXR5vnhVAFAonWXBMCtcfYyniwrnIQh2oVzktF27H5qj7IkGtaiNITsbj1j2u5HFDXbATNLSlK9U8XXnkP5dUAyBJe941I9r6ZxTJQv8Z_SNNb8NzQ4ed4GZTV8q9FF9jYNBR81FiHGucWTTVr3H2zmj6zo9ggk87RmtadTd-0BkGBICS6gMHR2Lh5LVzOFxL73_PfsNVwoZNPZRrmy7jRdm9BFGJByVxjYXslSH4dRogYOdOiJnaCpBewPgMs6ixkoro1welD0KaiJvI";

    public static final String CLINET_ID = "713aecdf1382415687f38d64a72b036b";
    public static final String SECRET_KEY =
            "fgJwX6sdQHL5YMJK6Kvdph7GV8Nv6k1OcyhCO0oa";
    public static final String SOLAR_SYSTEM_ID_JITA = "30000142";
    public static final String STATION_ID_RF_WINTERCO = "1031792377493";
    public static final String REGION_ID_OASA = "10000040";
    public static final String REGION_ID_THE_FROGE = "10000002";
    public static final double SELL_FAX = 0.028;
    public static final double AVG_BROKER_FAX = 0.03;
    public static final int EXPRESS_FAX_CUBIC_METRES = 1400;

    public static final String PATH_MARKET_DATA_FILE_FOLDER = "result/trade/marketData";
    public static final String PATH_JITA_INVENTORY = "result/trade/inventory/jitaInventory";
    public static final String PATH_RF_INVENTORY = "result/trade/inventory/rfInventory";
    public static final String PATH_RECOMMEND_NOT_BUY = "result/trade/recommendNotBuyList";
    public static final String PATH_RECOMMEND_BUY_SIMPLE = "result/trade/simple";

    public static final String SEPARATOR_PREFIX = "----";
}
