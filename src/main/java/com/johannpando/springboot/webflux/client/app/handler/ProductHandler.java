package com.johannpando.springboot.webflux.client.app.handler;

import java.net.URI;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.johannpando.springboot.webflux.client.app.dto.ImageProductDTO;
import com.johannpando.springboot.webflux.client.app.dto.Product;
import com.johannpando.springboot.webflux.client.app.service.IProductService;

import reactor.core.publisher.Mono;

@Component
public class ProductHandler {
	
	private static final Logger log = LoggerFactory.getLogger(ProductHandler.class);
	
	private final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private IProductService iProductService;
	
	public Mono<ServerResponse> list(ServerRequest request) {
		return ServerResponse.
				ok(). // Indicate a successful response
				contentType(MediaType.APPLICATION_JSON) // Set the response content type to JSON
				.body(iProductService.findAll(), Product.class); // Set the response body with the list of products
	}
	
	public Mono<ServerResponse> findById(ServerRequest request) {
		 String id = request.pathVariable("id"); // Extract the 'id' path variable from the request
		 return errorHandler(
			iProductService.findById(id) // Fin the product by ID
				.flatMap(p -> 
			 		ServerResponse
			 		.ok() // Indicate a successful response
			 		.contentType(MediaType.APPLICATION_JSON) // Set the response content type to JSON
					// .syncBody(p) // Deprecated
					.bodyValue(p) // Set the response with the found product
				)
				.switchIfEmpty(ServerResponse.notFound().build()) // If the product is not found, return a 404 response
			);			
	}
	
	public Mono<ServerResponse> create(ServerRequest request) {
		Mono<ImageProductDTO> productImageDTOMono = request.bodyToMono(ImageProductDTO.class);
		
		return errorHandler(
			productImageDTOMono
				.flatMap(pi -> {
					Product p = pi.getProduct();
					String image = pi.getImageProduct();
					
					if (image != null) {
						byte[] imageDecode = Base64.getDecoder().decode(image); // Decode the base64 image
						p.setImage(imageDecode); // Set the decoded image to the product
					}
					return iProductService.save(pi);
				})
				.flatMap(p -> 
					ServerResponse.created(URI.create("/api/client/".concat(p.getId()))) // Indicate a resource creation response with the product ID in the URI
					.contentType(MediaType.APPLICATION_JSON) // Set the response content type to JSON
					.bodyValue(p)) // Set the response body with the saved product
				);
	}

	public Mono<ServerResponse> update(ServerRequest request) {
		// Extract the request body and convert it to Product
		Mono<Product> product = request.bodyToMono(Product.class);
		// Extract the 'id' path variable from the request
		String id = request.pathVariable("id");
		
		return errorHandler( 
			product
			.flatMap(p -> iProductService.update(p, id)) // Update the product with the given ID
			.flatMap(p -> ServerResponse.created(URI.create("/api/client/".concat(id))) // Indicate a resource creation response with the product ID in the URI
			.contentType(MediaType.APPLICATION_JSON) // Set the response content type to JSON
			// .syncBody(product) // Deprecated
			.bodyValue(p)) // Set the response body with the updated product
		);
		
	}

	
	
	public Mono<ServerResponse> delete(ServerRequest request) {
		// Extract the 'id' path variable from the request
		String id = request.pathVariable("id");

		 return errorHandler(
			 iProductService.delete(id) // Delete the product with the given ID
			 	.then(ServerResponse.noContent().build()) // Indicate a no content (204) response after successful deletion
			 );
		 
	}
	
	private Mono<ServerResponse> errorHandler(Mono<ServerResponse> response) {
		return response
				.onErrorResume(error -> {
					WebClientResponseException responseError = (WebClientResponseException) error;
					Map<String, Object> errorMap = new HashMap<>();
					errorMap.put("Timestamp: ", new Date());
					errorMap.put("Status: ", responseError.getStatusCode());
					errorMap.put("Error detail: ", responseError.getMessage());
					if (responseError.getStatusCode() == HttpStatus.NOT_FOUND) {
						errorMap.put("Error: ", "The product does not found: ".concat(responseError.getMessage()));
						//return ServerResponse.notFound().build();
						return ServerResponse.status(responseError.getStatusCode())
							.bodyValue(errorMap);
					} else if (responseError.getStatusCode() == HttpStatus.BAD_REQUEST) {
						String errorList = responseError.getResponseBodyAsString();
						List<String> errorStringList = new ArrayList<>();
						try {
							errorStringList = objectMapper.readValue(errorList, new TypeReference<List<String>>() {});
						} catch (JsonProcessingException e) {
							log.error(e.getMessage());
						}
						errorMap.put("Error list: ", errorStringList);
						return ServerResponse.status(responseError.getStatusCode())
								.bodyValue(errorMap);
					}
					return Mono.error(responseError);
				 });
	}

}
