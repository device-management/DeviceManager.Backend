package com.nocotom.dm.bootstrap;

import org.atmosphere.cpr.*;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.*;

@Configuration
public class AtmosphereBootstrapper {

    @Bean
    public AtmosphereServlet atmosphereServlet() {
        return new AtmosphereServlet();
    }

    @Bean
    public BroadcasterFactory broadcasterFactory(AtmosphereServlet servlet){
        return servlet.framework().getBroadcasterFactory();
    }

    @Bean
    public EmbeddedAtmosphereInitializer atmosphereInitializer() {
        return new EmbeddedAtmosphereInitializer();
    }

    @Bean
    public ServletRegistrationBean atmosphereServletBean(AtmosphereServlet servlet) {
        ServletRegistrationBean registration = new ServletRegistrationBean<Servlet>(servlet, "/channels/*");
        //registration.addInitParameter(ApplicationConfig.ANNOTATION_PACKAGE, "sample");
        registration.addInitParameter(ApplicationConfig.HEARTBEAT_INTERVAL_IN_SECONDS, "10");
        registration.setLoadOnStartup(0);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }

    private static class EmbeddedAtmosphereInitializer extends ContainerInitializer implements ServletContextInitializer {

        @Override
        public void onStartup(ServletContext servletContext) throws ServletException {
            onStartup(Collections.emptySet(), servletContext);
        }
    }
}
