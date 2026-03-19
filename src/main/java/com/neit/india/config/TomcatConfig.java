package com.neit.india.config;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> customizer() {
        return factory -> factory.addConnectorCustomizers((Connector connector) -> {
            connector.setMaxPostSize(20 * 1024 * 1024); // 20 MB
            connector.setMaxSavePostSize(20 * 1024 * 1024);
            System.out.println("Tomcat maxPostSize set to: " + connector.getMaxPostSize());
        });
    }
}