package com.test;

import io.swagger.models.auth.In;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParseHtml {
    public static void main(String[] args) throws Exception {
        File input = new File("C:\\Users\\Administrator\\Desktop\\html\\ceshi.html");
        Document doc = Jsoup.parse(input, "UTF-8", "");
        Elements table = doc.select("#lp");
        Element first = table.first();
        Elements children = first.children();
        Element tbody = children.get(1);
        Elements trList = tbody.children();
        int size = trList.size();
        for(int i = 0; i < size; i++) {
            Element tr = trList.get(i);
            Elements tdList = tr.children();
            int tdSize = tdList.size();
            for(int j = 0; j < tdSize; j++) {
                Element td = tdList.get(j);
                if(j == 4) {
                    System.out.println(td.html());
                } else {
                    System.out.print(td.text());
                }
                System.out.print("\t");
                if(j == tdSize - 1) {
                    System.out.println("");
                    System.out.println("========");
                }
            }
        }
    }
}
