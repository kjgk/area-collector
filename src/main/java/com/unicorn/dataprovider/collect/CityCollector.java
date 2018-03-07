package com.unicorn.dataprovider.collect;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.unicorn.dataprovider.model.Region;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class CityCollector implements Callable {

    private Region parent;

    public CityCollector(Region parent) {
        this.parent = parent;
    }

    public List<Region> call() {

        try {
            Thread.sleep(400L);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        System.out.println("【" + parent.getName() + "】开始采集");

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(MainCollector.baseUrl + "/" + parent.getLink())
                .get()
                .build();

        List<Region> regionList = new ArrayList();

        String responseText;
        try {

            Response response = client.newCall(request).execute();
            responseText = new String(response.body().bytes(), "gb2312");

            for (String row : StringUtils.substringsBetween(responseText, "<tr class='citytr'>", "</tr>")) {
                String link = StringUtils.substringBetween(row, "<td><a href='", "'>");
                String[] strings = StringUtils.substringsBetween(row, "'>", "</a>");
                String code = strings[0];
                String name = strings[1];

                Region region = new Region();
                region.setLink(link);
                region.setName(name);
                region.setCode(code);
                region.setParent_id(parent.getId());
                regionList.add(region);
            }

        } catch (Exception e) {
            e.printStackTrace();

            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }

            return this.call();
        }

        System.out.println("【" + parent.getName() + "】采集完成");

        return regionList;
    }
}
