package com.unicorn.dataprovider.collect;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.unicorn.dataprovider.model.Region;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class CountyCollector implements Callable {

    private Region parent;

    public CountyCollector(Region parent) {
        this.parent = parent;
    }

    public List<Region> call() {

        try {
            Thread.sleep(500L);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        System.out.println("[" + parent.getName() + "]开始采集");

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(MainCollector.baseUrl + "/" + parent.getLink())
                .get()
                .build();

        List<Region> regionList = new ArrayList();

        try {

            Response response = client.newCall(request).execute();
            String responseText = new String(response.body().bytes(), "gb2312");

            for (String row : StringUtils.substringsBetween(responseText, "<tr class='countytr'>", "</tr>")) {

                if (row.indexOf("市辖区") >= 0) {
                    continue;
                }

                String link = StringUtils.substringBetween(row, "<td><a href='", "'>");
                String[] strings;
                if (link != null) {
                    strings = StringUtils.substringsBetween(row, "'>", "</a>");
                } else {
                    strings = StringUtils.substringsBetween(row, "<td>", "</td>");
                }

                String code = strings[0];
                String name = strings[1];

                Region region = new Region();
                if (link != null) {
                    region.setLink(parent.getLink().split("/")[0] + "/" + link);
                }
                region.setName(name);
                region.setCode(code);
                region.setParent_id(parent.getId());
                regionList.add(region);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return this.call();
        }

        System.out.println("[" + parent.getName() + "]采集完成");

        return regionList;
    }
}
