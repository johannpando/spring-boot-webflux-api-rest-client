package com.johannpando.springboot.webflux.client.app.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.johannpando.springboot.webflux.client.app.dto.ImageProductDTO;
import com.johannpando.springboot.webflux.client.app.dto.Product;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ProductServiceImpl implements IProductService{
	
	// Non-blocking, reactive client to perform HTTP requests, exposing a fluent, 
	// reactive API over underlying HTTP client libraries such as Reactor Netty.
	@Autowired
	private WebClient.Builder webClient;

	@Override
	public Flux<Product> findAll() {
		return webClient.build()
			.get() // Make a GET request
			.accept(MediaType.APPLICATION_JSON) // Accept JSON response
			//.exchange() // Deprecated
			.retrieve() // Execute the request and retrieve the response
			.bodyToFlux(Product.class); // Convert the response body to a Flux of Product
	}

	@Override
	public Mono<Product> findById(String id) {
		return webClient.build()
			.get().uri("/{id}", Collections.singletonMap("id", id)) // Make a GET request with the provided ID in the URI
			.accept(MediaType.APPLICATION_JSON) // Accept JSON response
			.retrieve() // Execute the request and retrieve the response
			.bodyToMono(Product.class); // Convert the response body to a Mono of Product object
	}

	@Override
	public Mono<Product> save(ImageProductDTO imageProductDTO) {
		return webClient.build()
			.post() // Make a POST request
			.accept(MediaType.APPLICATION_JSON) // Accept a JSON response
			.contentType(MediaType.APPLICATION_JSON) // Set the request content type to JSON
			.bodyValue(imageProductDTO) // Set the request body with the provided imageProductDTO
			.retrieve() // Execute the request and retrieve the response
			.bodyToMono(Product.class); // Convert the response body to a Mono of Product object
	}

	@Override
	public Mono<Product> update(Product productDTO, String id) {
		return webClient.build()
				.put().uri("/{id}", Collections.singletonMap("id", id)) // Make a PUT request with the provided ID in the URI
				.accept(MediaType.APPLICATION_JSON) // Accept a JSON response
				.contentType(MediaType.APPLICATION_JSON) // Set the request content type to JSON
				// .syncBody(id) // Deprecated
				.bodyValue(productDTO) // Set the request body with the provided productDTO object
				.retrieve() // Execute the request and retrieve the response
				.bodyToMono(Product.class); // Convert the response body to a Mono of Product object
	}

	@Override
	public Mono<Void> delete(String id) {
		return webClient.build()
			.delete().uri("/{id}", Collections.singletonMap("id", id)) // Make a DELETE request with the provided ID in the URI
			.retrieve() // Execute the request and retrieve the response
			.bodyToMono(Void.class); // Convert the response body to a Mono Void
	}

}
