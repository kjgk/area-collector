package com.unicorn.dataprovider;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(
        prefix = "snowflake"
)
@Getter
@Setter
public class SnowflakeIdConfigurer {

    private Integer workerId = 0;

    private Integer dataCenterId = 0;

    @Bean(name = "snowflakeIdWorker")
    public SnowflakeIdWorker snowflakeIdWorker() {

        return new SnowflakeIdWorker(workerId, dataCenterId);
    }
}
