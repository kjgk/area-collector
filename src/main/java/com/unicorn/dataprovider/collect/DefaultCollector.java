package com.unicorn.dataprovider.collect;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.unicorn.dataprovider.RegionLevel;
import com.unicorn.dataprovider.model.Region;
import com.unicorn.dataprovider.service.RegionService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

@Component
@Scope("prototype")
public class DefaultCollector implements Callable {

    @Autowired
    private RegionService regionService;

    private final static Logger logger = LoggerFactory.getLogger(DefaultCollector.class);

    private Integer level;

    private List<Region> regionList;

    public DefaultCollector(Integer level) {
        this.level = level;
    }

    public DefaultCollector(Integer level, List<Region> regionList) {
        this.level = level;
        this.regionList = regionList;
    }


    public Object call() {

        while (true) {

            OkHttpClient client = new OkHttpClient();

            client.setReadTimeout(3, TimeUnit.SECONDS);

            boolean once = false;

            if (CollectionUtils.isEmpty(regionList)) {
                regionList = regionService.getRegion(level - 1);
            } else {
                once = true;
            }

            for (Region region : regionList) {
                if (region.getStatus() == 1 || StringUtils.isEmpty(region.getLink())) {
                    continue;
                }

                logger.info("开始采集【" + region.getName() + "】");

                Request request = new Request.Builder()
                        .url(MainCollector.baseUrl + "/" + region.getLink())
                        .get()
                        .build();

                String responseText;
                try {
                    Response response = client.newCall(request).execute();
                    responseText = new String(response.body().bytes(), "gb2312");
                    for (Region subRegion : processRow(region, responseText)) {
                        subRegion.setParent_id(region.getId());
                        regionService.save(subRegion);
                    }
                    regionService.complete(region.getId());
                    logger.info("采集成功【" + region.getName() + "】");

                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("采集失败【" + region.getName() + "】，" + e.toString());
                }
            }


            if (once) {
                return null;
            }

            try {
                Thread.sleep(1000 * 60);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private List<Region> processRow(Region parentRegion, String responseText) {

        List<Region> regionList = new ArrayList();

        boolean townCity = false;

        if (level == RegionLevel.CITY) {
            for (String row : StringUtils.substringsBetween(responseText, "<tr class='citytr'>", "</tr>")) {
                String link = StringUtils.substringBetween(row, "<td><a href='", "'>");
                String[] strings = StringUtils.substringsBetween(row, "'>", "</a>");
                String code = strings[0];
                String name = strings[1];

                Region region = new Region();
                region.setLink(link);
                region.setName(name);
                region.setCode(code);
                region.setLevel(level);
                regionList.add(region);
            }
        }
        if (level == RegionLevel.COUNTY) {

            // todo 不辖县、区、县级市的地级市 如：东莞市
//            if (responseText.indexOf("市辖区") == -10) {
//                level = level + 1;
//                townCity = true;
//            } else {
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
                        region.setLink(parentRegion.getLink().split("/")[0] + "/" + link);
                    }
                    region.setName(name);
                    region.setCode(code);
                    region.setLevel(level);
                    regionList.add(region);
                }
//            }
        }
        if (level == RegionLevel.TOWN) {
            for (String row : StringUtils.substringsBetween(responseText, "<tr class='towntr'>", "</tr>")) {
                String link = StringUtils.substringBetween(row, "<td><a href='", "'>");
                String[] strings = StringUtils.substringsBetween(row, "'>", "</a>");
                String code = strings[0];
                String name = strings[1];
                Region region = new Region();
                if (townCity) {
                    region.setLink(parentRegion.getLink().split("/")[0] + "/" + link);
                } else {
                    region.setLink(parentRegion.getLink().split("/")[0] + "/" + parentRegion.getLink().split("/")[1] + "/" + link);
                }
                region.setName(name);
                region.setCode(code);
                region.setLevel(level);
                regionList.add(region);
            }
        }
        if (level == RegionLevel.VILLAGE) {
            for (String row : StringUtils.substringsBetween(responseText, "<tr class='villagetr'>", "</tr>")) {
                String[] strings = StringUtils.substringsBetween(row, "<td>", "</td>");
                String code = strings[0];
                String villageType = strings[1];
                String name = strings[2];

                Region region = new Region();
                region.setName(name);
                region.setCode(code);
                region.setLevel(level);
                regionList.add(region);
            }
        }


        return regionList;
    }
}
