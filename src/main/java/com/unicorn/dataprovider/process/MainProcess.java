package com.unicorn.dataprovider.process;

import com.unicorn.dataprovider.RegionLevel;
import com.unicorn.dataprovider.model.Region;
import com.unicorn.dataprovider.service.RegionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class MainProcess {

    @Autowired
    private RegionService regionService;

    @Autowired
    @Qualifier("secondaryJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    private final static Logger logger = LoggerFactory.getLogger(MainProcess.class);

    @Scheduled(fixedDelay = 10000000000l)
    public void run() throws Exception {

        List<Region> regionList = regionService.getRegion(RegionLevel.PROVINCE);

        fn(regionList, null);
    }

    private void fn(List<Region> regionList, String parentId) {

        int orderNo = 10;
        for (Region region : regionList) {
            String objectId = UUID.randomUUID().toString().replaceAll("-", "").toLowerCase();
            jdbcTemplate.update("INSERT INTO public.med_region(\n" +
                            "            objectid, created_date, last_update_date, deleted, description, \n" +
                            "            name, order_no, created_by, last_updated_by, parent_id, code, \n" +
                            "            level)\n" +
                            "    VALUES (?, now(), now(), 0, null, \n" +
                            "            ?, ?, '36e6754b82894343919a6b42a1a3216d', '36e6754b82894343919a6b42a1a3216d', ?, ?, \n" +
                            "            ?);\n",
                    objectId, region.getName(), orderNo, parentId, region.getCode(), region.getLevel()
            );
            orderNo += 10;

            if (region.getLevel() < 5) {
                logger.info("正在处理" + region.getName());
                fn(regionService.getRegionByParentId(region.getId()), objectId);
            }


        }
    }
}
