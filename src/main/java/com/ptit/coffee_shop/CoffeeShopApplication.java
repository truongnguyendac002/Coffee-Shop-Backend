package com.ptit.coffee_shop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class CoffeeShopApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoffeeShopApplication.class, args);
	}


}
