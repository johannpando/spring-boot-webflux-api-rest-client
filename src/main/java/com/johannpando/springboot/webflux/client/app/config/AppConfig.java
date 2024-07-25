package com.johannpando.springboot.webflux.client.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

	@Value("${config.base.endpoint}")
	private String configBaseEndpoint;

    @Bean
    @LoadBalanced
    WebClient.Builder registerWebClient() {
		return WebClient.builder().baseUrl(configBaseEndpoint);
	}
}
