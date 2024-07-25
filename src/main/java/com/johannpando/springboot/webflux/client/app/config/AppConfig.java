package com.johannpando.springboot.webflux.client.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AppConfig {

	@Value("${config.base.endpoint}")
	private String configBaseEndpoint;

    @Bean
    // Non-blocking, reactive client to perform HTTP requests, exposing a fluent, 
    // reactive API over underlying HTTP client libraries such as Reactor Netty.
    WebClient registerWebClient() {
		return WebClient.create(configBaseEndpoint);
	}
}
