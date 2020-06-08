package com.eve.entity;

import com.eve.entity.database.Items;
import lombok.Data;

@Data
public class IndustryProduct {
    private Integer typeID;
    private Integer blueprintTypeID;
    private double PurchasePrice;
    private int flowQuantity;
    private double StationPrice;
    private int dailyVolume;
    private int recommendCount;

    private boolean t2 = false;
    private int remainRun = -1;
    private int RunInventoryFee = 0;
}
