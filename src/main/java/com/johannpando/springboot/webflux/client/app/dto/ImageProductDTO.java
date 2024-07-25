package com.johannpando.springboot.webflux.client.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ImageProductDTO {
	
	private Product product;
	
	private String imageProduct;

}
