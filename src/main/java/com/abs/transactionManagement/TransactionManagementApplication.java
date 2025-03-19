package com.abs.transactionManagement;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class TransactionManagementApplication {



	public static void main(String[] args) {
		System.setProperty("jdk.tls.client.protocols", "TLSv1,TLSv1.1,TLSv1.2,TLSv1.3");
		SpringApplication.run(TransactionManagementApplication.class, args);
	}

}
