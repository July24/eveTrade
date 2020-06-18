package com.test;

import com.eve.dao.CorpExchangeMapper;
import com.eve.dao.CorpExchangeMaterialsMapper;
import com.eve.entity.database.CorpExchange;
import com.eve.entity.database.CorpExchangeMaterials;
import com.eve.service.ServiceBase;
import io.swagger.models.auth.In;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ParseHtml extends ServiceBase {
    public static void main(String[] args) throws Exception {
        ParseHtml ph = new ParseHtml();
        String filepath = "D:\\yhy\\html\\test.html";
        List<ExchangeParse> exchangeParseList = ph.getExchangeParseList(filepath, 137);
        ph.insertDb(exchangeParseList);
    }

    private void insertDb(List<ExchangeParse> exchangeParseList) {
        CorpExchangeMapper corpExchangeMapper = getCorpExchangeMapper();
        CorpExchangeMaterialsMapper corpExchangeMaterialsMapper = getCorpExchangeMaterialsMapper();
        for(ExchangeParse parse : exchangeParseList) {
            corpExchangeMapper.insert(parse);
            List<CorpExchangeMaterials> exchangeMaterialsList = parse.getExchangeMaterialsList();
            for(CorpExchangeMaterials corpExchangeMaterials : exchangeMaterialsList) {
                corpExchangeMaterialsMapper.insert(corpExchangeMaterials);
            }
        }
        sqlSession.commit();
    }

    private List<ExchangeParse> getExchangeParseList(String filepath, int checkSize) throws Exception {
        File input = new File(filepath);
        Document doc = Jsoup.parse(input, "UTF-8", "");
        Elements table = doc.select("#lp");
        String titleTxt = doc.select("head > title").text();
        String[] split = titleTxt.split("-");
        String corpName = split[2].trim();
        Element first = table.first();
        Elements children = first.children();
        Element tbody = children.get(1);
        Elements trList = tbody.children();
        int size = trList.size();

        List<ExchangeParse> ret = new ArrayList<>();

        for(int i = 0; i < size; i++) {
            ExchangeParse exchangeParse = new ExchangeParse();
            exchangeParse.setCorporationName(corpName);
            Element tr = trList.get(i);
            Elements tdList = tr.children();
            int tdSize = tdList.size();
            for(int j = 0; j < tdSize; j++) {
                Element td = tdList.get(j);
                switch (j) {
                    case 1 :
                        int lp = Integer.parseInt(td.text().replace(",", ""));
                        exchangeParse.setLpCost(lp);
                        break;
                    case 2 :
                        int isk = Integer.parseInt(td.text().replace(",", ""));
                        exchangeParse.setIskCost(isk);
                        break;
                    case 3 :
                        exchangeParse.setItemName(td.text());
                        break;
                    case 4 :
                        Element materialTbody = td.select("table > tbody").get(0);
                        Elements materialTr = materialTbody.children();
                        int mtSize = materialTr.size();
                        for(int k = 1; k < mtSize; k++){
                            CorpExchangeMaterials exMaterials = new CorpExchangeMaterials();
                            Element mtTr = materialTr.get(k);
                            String mtQuantity = mtTr.select("td:nth-child(1)").text();
                            exMaterials.setMaterialsQuantity(Integer.parseInt(mtQuantity));
                            String mtName = mtTr.select("td:nth-child(2)").text();
                            exMaterials.setMaterialsName(mtName);
                            exMaterials.setCorporationName(corpName);
                            exMaterials.setItemName(exchangeParse.getItemName());
                            exchangeParse.addExchangeMaterials(exMaterials);
                        }
                        break;
                    case 6 :
                        exchangeParse.setQuantity(Integer.parseInt(td.text()));
                        break;
                }
            }
            ret.add(exchangeParse);
        }
        if(checkSize != ret.size()) {
            throw new Exception("解析数量不对");
        }
        return ret;
    }
}
