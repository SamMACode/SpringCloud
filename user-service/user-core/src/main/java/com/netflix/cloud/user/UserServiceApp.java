package com.netflix.cloud.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author Sam Ma
 * @date 2019/09/19
 */
@SpringBootApplication
@EnableDiscoveryClient
public class UserServiceApp {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApp.class, args);
	}
}
