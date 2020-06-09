package com.eve.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProductMaterial {
    private List<MaterialOrigin> materialOriginList = new ArrayList<>();

    public void addMaterialOrigin(MaterialOrigin origin) {
        materialOriginList.add(origin);
    }
}
