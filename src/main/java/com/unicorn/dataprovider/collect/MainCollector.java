package com.unicorn.dataprovider.collect;

import com.unicorn.dataprovider.model.Region;
import com.unicorn.dataprovider.service.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Component
public class MainCollector {

    public static String baseUrl = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2016";

    @Autowired
    private RegionService regionService;

    @Scheduled(fixedDelay = 10000000000l)
    public void run() throws Exception {


        ExecutorService executorService = Executors.newSingleThreadExecutor();

        List<Future<List<Region>>> futureList = new ArrayList<>();

        List<Region> provinceList = (List<Region>) executorService.submit(new ProvinceCollector()).get();
        List<Region> cityList = new ArrayList();
        List<Region> countyList = new ArrayList();
        List<Region> townList = new ArrayList();
        List<Region> villageList = new ArrayList();


        System.out.println("开始采集城市，共" + provinceList.size() + "个");
        for (Region region : provinceList) {
            Region _region = regionService.getRegion(region.getCode());
            if (_region == null) {
                regionService.save(region);
            } else {
                if (_region.getStatus() == 1) {
                    continue;
                }
                region.setId(_region.getId());
            }
            if (region.getLink() == null) {
                continue;
            }
            futureList.add(executorService.submit(new CityCollector(region)));
        }
        for (Future<List<Region>> future : futureList) {
            List<Region> regions = future.get();
            for (Region region : regions) {
                Region _region = regionService.getRegion(region.getCode());
                if (_region == null) {
                    regionService.save(region);
                } else {
                    if (_region.getStatus() == 1) {
                        continue;
                    }
                    region.setId(_region.getId());
                }
            }

            regionService.complete(regions.get(0).getParent_id());
            cityList.addAll(regions);
        }
        futureList.clear();


        System.out.println("开始采集区县，共" + cityList.size() + "个");
        for (Region region : cityList) {
            Region _region = regionService.getRegion(region.getCode());
            if (_region == null) {
                regionService.save(region);
            } else {
                if (_region.getStatus() == 1) {
                    continue;
                }
                region.setId(_region.getId());
            }
            if (region.getLink() == null) {
                continue;
            }
            futureList.add(executorService.submit(new CountyCollector(region)));
        }
        for (Future<List<Region>> future : futureList) {
            List<Region> regions = future.get();
            regionService.complete(regions.get(0).getParent_id());
            countyList.addAll(regions);
        }
        futureList.clear();

//
//        System.out.println("开始采集乡镇/街道，共" + countyList.size() + "个");
//        for (Region region : countyList) {
//            if (region.getLink() == null) {
//                continue;
//            }
//            futureList.add(executorService.submit(new TownCollector(region)));
//        }
//        for (Future<List<Region>> future : futureList) {
//            townList.addAll(future.get());
//        }
//        futureList.clear();
//
//
//        System.out.println("开始采集村/居委会，共" + townList.size() + "个");
//        for (Region region : townList) {
//            if (region.getLink() == null) {
//                continue;
//            }
//            futureList.add(executorService.submit(new VillageCollector(region)));
//        }
//        for (Future<List<Region>> future : futureList) {
//            villageList.addAll(future.get());
//        }
//        futureList.clear();


    }
}
