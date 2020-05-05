package com.eve.entity;

import lombok.Data;

@Data
public class EveOrder {
    private int duration;
    private boolean isBuyOrder;
    private String issued;
    private String locationId;
    private int minVolume;
    private String orderId;
    private double price;
    private String range;
    private int typeId;
    private int volumeRemain;
    private int volumeTotal;
}
