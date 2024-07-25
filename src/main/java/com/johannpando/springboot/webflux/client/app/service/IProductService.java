package com.johannpando.springboot.webflux.client.app.service;

import com.johannpando.springboot.webflux.client.app.dto.ImageProductDTO;
import com.johannpando.springboot.webflux.client.app.dto.Product;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IProductService {

	Flux<Product> findAll();
	
	Mono<Product> findById(String id);
	
	Mono<Product> save(ImageProductDTO imageProductDTO);
	
	Mono<Product> update(Product productDTO, String id);
	
	Mono<Void> delete(String id);
}
