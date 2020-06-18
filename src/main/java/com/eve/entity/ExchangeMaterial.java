package com.eve.entity;

import com.eve.entity.database.CorpExchangeMaterials;
import com.test.ExchangeParse;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ExchangeMaterial {
    private String itemName;
    private int exchangeTimes;
    private int prodCnt;
    private List<CorpExchangeMaterials> corpExchangeMaterialsList = new ArrayList<>();

    public void addExchangeMaterials(CorpExchangeMaterials materials) {
        corpExchangeMaterialsList.add(materials);
    }
}
