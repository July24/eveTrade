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

    public static void main(String[] args) {

        writeCSV();
    }

    public static void writeCSV() {
        try {
            Appendable out = new PrintWriter("item.csv");
            CSVPrinter csvPrinter = CSVFormat.DEFAULT.print(out);
            Yaml yaml = new Yaml();
//            URL url = LoadYaml.class.getClassLoader().getResource("test.yaml");
            File yamlFile = new File("D:\\ideaproject\\loadYamlFile\\src\\main\\resources\\typeIDs.yaml");
            csvPrinter.printRecord("id","basePrice","graphicID","groupID","iconID","marketGroupID","metaGroupID","cn_name","en_name","volume");
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
//                    Object metaGroupID = item.get("metaGroupID");
//                    if(metaGroupID == null) {
//                        continue;
//                    }
//                    if(!"1".equals(String.valueOf(metaGroupID))) {
//                        continue;
//                    }
//                    Object metaGroupID = item.get("metaGroupID");
//                    if(metaGroupID != null) {
//                        continue;
//                    }
//                    String en = String.valueOf(name.get("en"));
//                    if(!en.contains("Blueprint")) {
//                        continue;
//                    }
//                    csvPrinter.printRecord(id, name.get("zh"), name.get("en"), item.get("volume"));
                    csvPrinter.printRecord(id, item.get("basePrice"),item.get("graphicID"),item.get("groupID"),
                            item.get("iconID"),item.get("marketGroupID"), item.get("metaGroupID"),name.get("zh"), name.get("en"), item.get("volume"));
                }
            }
            csvPrinter.flush();
            csvPrinter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
