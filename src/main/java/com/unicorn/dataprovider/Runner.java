package com.unicorn.dataprovider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class Runner implements ApplicationListener, ApplicationContextAware {

    private final static Logger logger = LoggerFactory.getLogger(Runner.class);

    private static ApplicationContext context;


    @Override
    public void onApplicationEvent(ApplicationEvent applicationEvent) {

        if (applicationEvent instanceof ApplicationReadyEvent) {
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        context = applicationContext;
    }
}
