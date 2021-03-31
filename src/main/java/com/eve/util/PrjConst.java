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
    public static final String LIST_REGION_ORDER_URL = "https://esi.evetech.net/latest/markets/{region_id}/orders/";
    public static final String LIST_REGION_ITEM_ID = "https://esi.evetech.net/latest/markets/{region_id}/types/";
    public static final String GET_CHAR_BLUEPRINT_URL = "https://esi.evetech.net/latest/characters/{character_id" +
            "}/blueprints/";

    public static final String DATASOURCE = "tranquility";
    public static final String TOKEN_TYPE = "Bearer";

    public static final String CLINET_ID = "713aecdf1382415687f38d64a72b036b";
    public static final String SECRET_KEY =
            "fgJwX6sdQHL5YMJK6Kvdph7GV8Nv6k1OcyhCO0oa";
    public static final String SOLAR_SYSTEM_ID_JITA = "30000142";
    public static final String STATION_ID_RF_WINTERCO = "1031792377493";
    public static final String STATION_ID_JITA_NAVY4 = "60003760";
    public static final String REGION_ID_OASA = "10000040";
    public static final String REGION_ID_THE_FORGE = "10000002";
    public static final double SELL_FAX = 0.0225;
    public static final double AVG_BROKER_FAX = 0.03;
    public static final int EXPRESS_FAX_CUBIC_METRES = 1400;
    public static final int EXPRESS_FAX_CUBIC_METRES_RF_TO_JITA = 1000;
    public static final double EXPRESS_FAX_RF_TO_JITA_EXTRA = 0.02;

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

    public static final double INVENTION_BASE_MODULES_RIGS_AMMO = 0.34;

    public static final int INVENTION_BPC_DFT_RUNS_SHIP_RIG = 1;
    public static final int INVENTION_BPC_DFT_RUNS_OTHER = 10;
    public static final double INVENTION_BPC_DFT_ME = 0.02;
    public static final double INVENTION_BPC_DFT_TE = 0.04;


    public static final double INVENTION_RIG_SUCCESS_L4 = 0.4647;
    public static final double INVENTION_RIG_SUCCESS_AUGMENTATION_L4 = 0.2788;
    public static final double INVENTION_RIG_SUCCESS_L3 = 0.4335;
    public static final double INVENTION_RIG_SUCCESS_AUGMENTATION_L3 = 0.2601;

    public static final int DECRYPTOR_AUGMENTATION_ID = 34203;

}
