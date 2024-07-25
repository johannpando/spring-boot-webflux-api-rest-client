package com.johannpando.springboot.webflux.client.app.dto;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Product {

	private String id;
	
	private String name;
	
	private Double price;
	
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private Date createAt;
	
	private byte[] image;
	
	private Category category;
	
	public Product(String name, double price) {
		this.name = name;
		this.price = price;
	}

	public Product(String name, double price, Category category) {
		this.name = name;
		this.price = price;
		this.category = category;
	}
}
