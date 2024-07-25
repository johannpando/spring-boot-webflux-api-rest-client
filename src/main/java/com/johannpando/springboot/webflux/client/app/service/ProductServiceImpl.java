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
	
	@Autowired
	private WebClient webClient;

	@Override
	public Flux<Product> findAll() {
		return webClient
			.get()
			.accept(MediaType.APPLICATION_JSON)
			//.exchange() // Deprecated
			.retrieve()
			.bodyToFlux(Product.class)
			;
	}

	@Override
	public Mono<Product> findById(String id) {
		return webClient
			.get().uri("/{id}", Collections.singletonMap("id", id))
			.accept(MediaType.APPLICATION_JSON)
			.retrieve()
			.bodyToMono(Product.class);
	}

	@Override
	public Mono<Product> save(ImageProductDTO imageProductDTO) {
		return webClient
			.post()
			.accept(MediaType.APPLICATION_JSON)
			.contentType(MediaType.APPLICATION_JSON)
			.bodyValue(imageProductDTO)
			.retrieve()
			.bodyToMono(Product.class);
	}

	@Override
	public Mono<Product> update(Product productDTO, String id) {
		return webClient
				.put().uri("/{id}", Collections.singletonMap("id", id))
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				// .syncBody(id) // Deprecated
				.bodyValue(productDTO)
				.retrieve()
				.bodyToMono(Product.class);
	}

	@Override
	public Mono<Void> delete(String id) {
		return webClient
			.delete().uri("/{id}", Collections.singletonMap("id", id))
			.retrieve()
			.bodyToMono(Void.class);
	}

}
