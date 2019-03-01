package com.unicorn.dataprovider.collect;

import com.unicorn.dataprovider.service.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class MainCollector {

    public static String baseUrl = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2018";

    @Autowired
    private RegionService regionService;

    @Scheduled(fixedDelay = 10000000000l)
    public void run() throws Exception {

        ExecutorService executorService = Executors.newCachedThreadPool();

        // 第一步采集省份
        // executorService.submit(ApplicationContextProvider.getBean(ProvinceCollector.class));

        // 第二步采集市区县（重复执行）
//        executorService.submit(ApplicationContextProvider.getBean(DefaultCollector.class, RegionLevel.CITY));
//        executorService.submit(ApplicationContextProvider.getBean(DefaultCollector.class, RegionLevel.COUNTY));
//        executorService.submit(ApplicationContextProvider.getBean(DefaultCollector.class, RegionLevel.TOWN));

//        executorService.submit(ApplicationContextProvider.getBean(DefaultCollector.class, RegionLevel.VILLAGE));

        /*
        // 如果街道数据用一下方式获取能减少并发，提高采集成功率
        List<Region> regionList = regionService.getRegion(RegionLevel.TOWN);

        int index = 0;
        int size = 1000;
        while (index < regionList.size()) {
            int end = Math.min(index + size, regionList.size());
            List<Region> subList = regionList.subList(index, end);
            executorService.submit(ApplicationContextProvider.getBean(DefaultCollector.class, RegionLevel.VILLAGE, subList));
            index = end;
        }*/
    }
}
