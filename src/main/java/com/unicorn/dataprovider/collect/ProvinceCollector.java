package com.unicorn.dataprovider.collect;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.unicorn.dataprovider.model.Region;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

//@Component
public class ProvinceCollector implements Callable {


    public List<Region> call() {

        try {
            Thread.sleep(100L);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(MainCollector.baseUrl + "/index.html")
                .get()
                .build();

        List<Region> regionList = new ArrayList();

        try {
            Response response = client.newCall(request).execute();
            String responseText = new String(response.body().bytes(), "gb2312");
            for (String row : StringUtils.substringsBetween(responseText, "<tr class='provincetr'>", "</tr>")) {
                String[] links = StringUtils.substringsBetween(row, "<a href='", "'>");
                String[] names = StringUtils.substringsBetween(row, "'>", "<br/>");
                for (int i = 0; i < links.length; i++) {
                    Region region = new Region();
                    region.setLink(links[i]);
                    region.setCode(links[i].replaceAll(".html", ""));
                    region.setName(names[i]);
                    regionList.add(region);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return this.call();
        }
        return regionList;
    }
}
