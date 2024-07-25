package com.johannpando.springboot.webflux.client.app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.johannpando.springboot.webflux.client.app.handler.ProductHandler;

@Configuration
public class RouterConfig {

	@Bean
	RouterFunction<ServerResponse> routes(ProductHandler handler) {
		//request -> handler.list(request));
		// It is the same
		return RouterFunctions.
			route(RequestPredicates.GET("/api/client"), handler::list)
			.andRoute(RequestPredicates.PUT("/api/client/{id}"), handler::update)
			.andRoute(RequestPredicates.GET("/api/client/{id}"), handler::findById)
			.andRoute(RequestPredicates.DELETE("/api/client/{id}"), handler::delete)
			.andRoute(RequestPredicates.POST("/api/client"), handler::create);
	}
}
