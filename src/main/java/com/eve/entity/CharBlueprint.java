package com.eve.entity;

import lombok.Data;

@Data
public class CharBlueprint {
    private Long itemId;
    private String locationFlag;
    private Long locationId;
    private Integer materialEfficiency;
    private Integer quantity;
    private Integer runs;
    private Integer timeEfficiency;
    private Integer typeId;
}
