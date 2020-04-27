package com.test;

import cn.hutool.http.HttpUtil;
import com.eve.entity.Fitting;
import com.eve.util.TradeUtil;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LoadYaml {
    public static void testHttp() {
        System.out.println(new Date(1587696264447L));
//        curl -X GET "https://api.evemarketer.com/ec/marketstat?typeid=200" -H "accept: application/xml"
//        HashMap<String, Object> paramMap = new HashMap<>();
//        paramMap.put("typeid", "200");
//
//        String result3= HttpUtil.get("https://api.evemarketer.com/ec/marketstat/json", paramMap);
//        System.out.println(result3);
    }


    public static void main(String[] args) {
        String a = "[Muninn, WC Muninn- DPS Muninn/ WC缪宁-火力缪宁]\n" +
                "Mark I Compact Power Diagnostic System\n" +
                "Gyrostabilizer II\n" +
                "Gyrostabilizer II\n" +
                "Tracking Enhancer II\n" +
                "Tracking Enhancer II\n" +
                "Assault Damage Control II\n" +
                "\n" +
                "Large Shield Extender II\n" +
                "Large Shield Extender II\n" +
                "Adaptive Invulnerability Field II\n" +
                "50MN Quad LiF Restrained Microwarpdrive\n" +
                "\n" +
                "720mm Howitzer Artillery II\n" +
                "720mm Howitzer Artillery II\n" +
                "720mm Howitzer Artillery II\n" +
                "720mm Howitzer Artillery II\n" +
                "720mm Howitzer Artillery II\n" +
                "\n" +
                "Medium Core Defense Field Extender I\n" +
                "Medium Core Defense Field Extender I\n" +
                "\n" +
                "\n" +
                "\n" +
                "Hornet EC-300 x5\n" +
                "\n" +
                "Republic Fleet EMP M x2000\n" +
                "Republic Fleet Phased Plasma M x1000\n" +
                "Tremor M x2000\n" +
                "Republic Fleet Proton M x2000\n" +
                "Republic Fleet Depleted Uranium M x2000\n" +
                "Republic Fleet Fusion M x2000\n" +
                "Republic Fleet Titanium Sabot M x1000\n" +
                "Nanite Repair Paste x100\n" +
                "Quake M x1000\n" +
                "10MN Afterburner II x1\n" +
                "Gyrostabilizer II x1";
        TradeUtil tradeUtil = new TradeUtil();
        Fitting fitting = tradeUtil.parseFittingText(a);
        System.out.println(fitting);
//        testText(a);
    }

    public static void writeCSV(String args) {
        try {
            Appendable out = new PrintWriter("typeIDs.csv");
            CSVPrinter csvPrinter = CSVFormat.DEFAULT.print(out);
            Yaml yaml = new Yaml();
//            URL url = LoadYaml.class.getClassLoader().getResource("test.yaml");
            File yamlFile = new File("D:\\ideaproject\\loadYamlFile\\src\\main\\resources\\test.yaml");
            if (yamlFile != null) {
                //获取test.yaml文件中的配置数据，然后转换为obj，
//                Object obj =yaml.load(new FileInputStream(url.getFile()));
//                System.out.println(obj);
                //也可以将值转换为Map
                Map map =(Map)yaml.load(new FileInputStream(yamlFile));
                //通过map我们取值就可以了.
                Iterator iter = map.keySet().iterator();
                while(iter.hasNext()) {
                    Integer id = (Integer) iter.next();
//                    System.out.println("id:" + id);
                    Map item = (Map)map.get(id);
                    Map name = (Map)item.get("name");
//                    System.out.println("中文名:" + name.get("zh"));
//                    System.out.println("英文名:" + name.get("en"));
//                    System.out.println("体积:" + item.get("volume"));
                    csvPrinter.printRecord(id, name.get("zh"), name.get("en"), item.get("volume"));
                }
            }
            csvPrinter.flush();
            csvPrinter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
