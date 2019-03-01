package com.unicorn.dataprovider.service;

import com.unicorn.dataprovider.SnowflakeIdWorker;
import com.unicorn.dataprovider.model.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class RegionService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;

    private final static Logger logger = LoggerFactory.getLogger(RegionService.class);

    public List<Region> getRegion(Integer level) {

        List regions = jdbcTemplate.queryForList("select * from region where level = ? order by code", level);
        return mapRegion(regions);
    }

    public List<Region> getRegionByParentId(Long parentId) {

        List regions = jdbcTemplate.queryForList("select * from region where parent_id = ? order by code", parentId);
        return mapRegion(regions);
    }

    private List<Region> mapRegion(List regions) {

        List<Region> regionList = new ArrayList();
        for (Object o : regions) {
            Map data = (Map) o;
            Region region = new Region();
            region.setId((Long) data.get("id"));
            region.setName((String) data.get("name"));
            region.setCode((String) data.get("code"));
            region.setParent_id((Long) data.get("parent_id"));
            region.setStatus((Integer) data.get("status"));
            region.setLevel((Integer) data.get("level"));
            region.setLink((String) data.get("link"));
            regionList.add(region);
        }
        return regionList;
    }


    public void save(Region region) {

        region.setId(snowflakeIdWorker.nextId());
        region.setStatus(0);

        String insertSql = "INSERT INTO region(\n" +
                "            id, name, code, parent_id, status, link, level)\n" +
                "    VALUES (?, ?, ?, ?, ?, ?, ?)";
//            String deleteSql = "DELETE FROM region where code = ?";
        while (true) {
            try {
                jdbcTemplate.update(insertSql
                        , region.getId()
                        , region.getName()
                        , region.getCode()
                        , region.getParent_id()
                        , region.getStatus()
                        , region.getLink()
                        , region.getLevel()
                );
                logger.debug("更新：" + region);
                break;
            } catch (DuplicateKeyException e) {
                break;
            }
        }
    }

    public void complete(Long id) {

        jdbcTemplate.update("update region set status = 1 where id = ?", id);
    }
}