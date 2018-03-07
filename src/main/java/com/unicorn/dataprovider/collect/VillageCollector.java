package com.unicorn.dataprovider.collect;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.unicorn.dataprovider.model.Region;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class VillageCollector implements Callable {

    private Region parent;

    public VillageCollector(Region parent) {
        this.parent = parent;
    }

    public List<Region> call() {

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        System.out.println("<" + parent.getName() + ">开始采集");

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(MainCollector.baseUrl + "/" + parent.getLink())
                .get()
                .build();

        List<Region> regionList = new ArrayList();

        try {
            Response response = client.newCall(request).execute();
            String responseText = new String(response.body().bytes(), "gb2312");

            for (String row : StringUtils.substringsBetween(responseText, "<tr class='villagetr'>", "</tr>")) {
                String[] strings = StringUtils.substringsBetween(row, "<td>", "</td>");
                String code = strings[0];
                String villageType = strings[1];
                String name = strings[2];

                Region region = new Region();
                region.setName(name);
                region.setCode(code);
                region.setParent_id(parent.getId());
                regionList.add(region);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return this.call();
        }

        System.out.println("<" + parent.getName() + ">采集完成");

        return regionList;
    }
}
