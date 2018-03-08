package com.unicorn.dataprovider.collect;

import com.unicorn.dataprovider.ApplicationContextProvider;
import com.unicorn.dataprovider.RegionLevel;
import com.unicorn.dataprovider.model.Region;
import com.unicorn.dataprovider.service.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class MainCollector {

    public static String baseUrl = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2016";

    @Autowired
    private RegionService regionService;

    @Scheduled(fixedDelay = 10000000000l)
    public void run() throws Exception {

        ExecutorService executorService = Executors.newCachedThreadPool();

//        executorService.submit(ApplicationContextProvider.getBean(ProvinceCollector.class));
//        executorService.submit(ApplicationContextProvider.getBean(DefaultCollector.class, RegionLevel.CITY));
//        executorService.submit(ApplicationContextProvider.getBean(DefaultCollector.class, RegionLevel.COUNTY));
//        executorService.submit(ApplicationContextProvider.getBean(DefaultCollector.class, RegionLevel.TOWN));


        List<Region> regionList = regionService.getRegion(RegionLevel.TOWN);

        int index = 0;
        int size = 5000;
        while (index < regionList.size()) {
            int end = Math.min(index + size, regionList.size());
            List<Region> subList = regionList.subList(index, end);
            executorService.submit(ApplicationContextProvider.getBean(DefaultCollector.class, RegionLevel.VILLAGE, subList));
            index = end;
        }
    }
}
