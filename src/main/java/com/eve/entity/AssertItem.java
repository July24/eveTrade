package com.eve.entity;

import lombok.Data;

@Data
public class AssertItem {
    private boolean isBlueprintCopy;
    private boolean isSingleton;
    private long itemId;
    private String locationFlag;
    private long locationId;
    private String locationType;
    private int quantity;
    private int typeId;
}
