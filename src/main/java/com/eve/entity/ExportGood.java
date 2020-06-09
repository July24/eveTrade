package com.eve.entity;

import lombok.Data;

@Data
public class ExportGood {
    private Integer typeID;
    private Integer blueprintTypeID;
    private double materialPrice;
    private double jitaBuyPrice;
    private double volume;
    private int expressFee = 0;

    private boolean t2 = false;
    private int runInventoryFee = 0;
}
