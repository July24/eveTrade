package com.eve.entity;

import lombok.Data;

import java.util.Date;

@Data
public class OrderHistory {
    private double average;
    private double highest;
    private double lowest;
    private int orderCount;
    private int volume;
    private Date date;
}
