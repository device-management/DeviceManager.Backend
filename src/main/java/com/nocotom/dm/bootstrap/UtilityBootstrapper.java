package com.nocotom.dm.bootstrap;

import com.google.common.eventbus.EventBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UtilityBootstrapper {

    @Bean
    public EventBus eventBus(){
        return new EventBus();
    }
}
