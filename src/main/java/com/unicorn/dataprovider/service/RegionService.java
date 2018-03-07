package com.unicorn.dataprovider.service;

import com.unicorn.dataprovider.model.Region;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class RegionService {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    private final static Logger logger = LoggerFactory.getLogger(RegionService.class);

    public Region getRegion(String code) {

        List regions = jdbcTemplate.queryForList("select * from region where code = ?", code);
        if (regions.size() > 0) {
            Map data = (Map) regions.get(0);
            Region region = new Region();
            region.setId((String) data.get("id"));
            region.setName((String) data.get("name"));
            region.setCode((String) data.get("code"));
            region.setParent_id((String) data.get("parent_id"));
            region.setStatus((Integer) data.get("status"));
            return region;
        }
        return null;
    }

    public void save(Region region) {

        region.setId(UUID.randomUUID().toString().replaceAll("-", "").toLowerCase());
        region.setStatus(0);

        String insertSql = "INSERT INTO region(\n" +
                "            id, name, code, parent_id, status)\n" +
                "    VALUES (?, ?, ?, ?, ?)";
//            String deleteSql = "DELETE FROM region where code = ?";
        while (true) {
            try {
                jdbcTemplate.update(insertSql
                        , region.getId()
                        , region.getName()
                        , region.getCode()
                        , region.getParent_id()
                        , region.getStatus()
                );
                logger.debug("更新：" + region);
                break;
            } catch (DuplicateKeyException e) {


                break;
            }
        }
    }

    public void complete(String id) {

        jdbcTemplate.update("update region set status = 1 where id = ?", id);
    }
}
