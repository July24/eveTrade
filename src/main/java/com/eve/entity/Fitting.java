package com.eve.entity;

import cn.hutool.core.math.MathUtil;
import cn.hutool.core.util.NumberUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Fitting {
    public static void main(String[] args) {
        List<Integer> typeIDs = new ArrayList<>();
        typeIDs.add(1);
        typeIDs.add(2);
        typeIDs.add(3);
        typeIDs.add(4);
        typeIDs.add(5);
        typeIDs.add(6);
        typeIDs.add(7);


        for (int i = 0; i < typeIDs.size(); i = i + 2) {
            int end = NumberUtil.min(i + 2, typeIDs.size());
            System.out.println(typeIDs.subList(i ,end));
        }
    }
    String name;
    String alias;
    List<String> highSlot = new ArrayList<>();
    List<String> middleSlot = new ArrayList<>();
    List<String> lowSlot = new ArrayList<>();
    List<String> rigging = new ArrayList<>();

    public void addHigh(String item) {
        highSlot.add(item);
    }
    public void addMiddle(String item) {
        middleSlot.add(item);
    }
    public void addLow(String item) {
        lowSlot.add(item);
    }
    public void addRigging(String item) {
        rigging.add(item);
    }
}
