package com.bao;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MsNettyApplication {

	public static void main(String[] args) {
		String a = "hello world";
		System.out.println(a.substring(6).concat(a.substring(0,6)));
		SpringApplication.run(MsNettyApplication.class, args);


	}
}
