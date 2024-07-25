package com.johannpando.springboot.webflux.client.app.handler;

import java.net.URI;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.johannpando.springboot.webflux.client.app.dto.ImageProductDTO;
import com.johannpando.springboot.webflux.client.app.dto.Product;
import com.johannpando.springboot.webflux.client.app.service.IProductService;

import reactor.core.publisher.Mono;

@Component
public class ProductHandler {
	
	private static final Logger log = LoggerFactory.getLogger(ProductHandler.class);

	@Autowired
	private IProductService iProductService;
	
	public Mono<ServerResponse> list(ServerRequest request) {
		return ServerResponse.
				ok().
				contentType(MediaType.APPLICATION_JSON)
				.body(iProductService.findAll(),
				Product.class);
	}
	
	public Mono<ServerResponse> findById(ServerRequest request) {
		 String id = request.pathVariable("id");

		 return iProductService.findById(id)
			.flatMap(p -> 
		 		ServerResponse
		 		.ok()
		 		.contentType(MediaType.APPLICATION_JSON)
				// .syncBody(p) // Deprecated
				.bodyValue(p)
				.switchIfEmpty(ServerResponse.notFound().build())
				.onErrorResume(error -> {
					WebClientResponseException responseError = (WebClientResponseException) error;
					if (responseError.getStatusCode() == HttpStatus.NOT_FOUND) {
						return ServerResponse.notFound().build();
					}
					return Mono.error(responseError);
			}));
	}
	
	public Mono<ServerResponse> create(ServerRequest request) {
		Mono<ImageProductDTO> productImageDTOMono = request.bodyToMono(ImageProductDTO.class);
		
		return productImageDTOMono
			.flatMap(pi -> {
				Product p = pi.getProduct();
				String image = pi.getImageProduct();
				
				if (image != null) {
					byte[] imageDecode = Base64.getDecoder().decode(image);
					p.setImage(imageDecode);
				}
				return iProductService.save(pi);
			})
			.flatMap(p -> 
				ServerResponse.created(URI.create("/api/client/".concat(p.getId())))
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(p))
			.onErrorResume(error -> {
				WebClientResponseException responseError = (WebClientResponseException) error;
				if (responseError.getStatusCode() == HttpStatus.BAD_REQUEST) {
					return ServerResponse.badRequest().build();
				}
				return Mono.error(responseError);
			});
	}

	public Mono<ServerResponse> update(ServerRequest request) {
		Mono<Product> product = request.bodyToMono(Product.class);
		String id = request.pathVariable("id");
		
		return product
			.flatMap(p -> iProductService.update(p, id))
			.flatMap(p -> ServerResponse.created(URI.create("/api/client/".concat(id)))
			.contentType(MediaType.APPLICATION_JSON)
			// .syncBody(product) // Deprecated
			.bodyValue(p))
		.onErrorResume(error -> {
			WebClientResponseException responseError = (WebClientResponseException) error;
			if (responseError.getStatusCode() == HttpStatus.BAD_REQUEST) {
				return ServerResponse.notFound().build();
			}
			return Mono.error(responseError);
		});
	}

	
	
	public Mono<ServerResponse> delete(ServerRequest request) {
		String id = request.pathVariable("id");

		 return iProductService.delete(id)
			.then(ServerResponse.noContent().build())
			.onErrorResume(error -> {
				WebClientResponseException responseError = (WebClientResponseException) error;
				if (responseError.getStatusCode() == HttpStatus.NOT_FOUND) {
					return ServerResponse.notFound().build();
				}
				return Mono.error(responseError);
			});
	}

}
