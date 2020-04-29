package com.eve.entity;

import lombok.Data;

@Data
public class BuyOrderRecord {
    private int id;
    private String en_name;
    private int count;
    private double price;
}
