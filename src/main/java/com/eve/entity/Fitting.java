package com.eve.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Fitting {
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
