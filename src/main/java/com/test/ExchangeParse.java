package com.test;

import com.eve.entity.database.CorpExchange;
import com.eve.entity.database.CorpExchangeMaterials;
import lombok.Data;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

@Data
public class ExchangeParse extends CorpExchange {
    private List<CorpExchangeMaterials> exchangeMaterialsList = new ArrayList<>();

    public void addExchangeMaterials(CorpExchangeMaterials exchangeMaterials) {
        exchangeMaterialsList.add(exchangeMaterials);
    }
}
