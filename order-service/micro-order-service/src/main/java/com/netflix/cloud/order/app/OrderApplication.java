package com.netflix.cloud.order.app;

import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 更简洁的写法是使用@SpringCloudApplication注解
 * @author dong
 * */
@EnableFeignClients(basePackages = "com.netflix.cloud.product.client")
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.netflix.cloud")
@EnableJpaRepositories(basePackages = "com.netflix.cloud.order.repository")
@EntityScan(basePackages = "com.netflix.cloud.order.dataobject")
@EnableCircuitBreaker
@EnableHystrixDashboard
@EnableBinding(Source.class)
public class OrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderApplication.class, args);
	}

	@Bean
	public ServletRegistrationBean getServlet() {
		/*
		 * hystrix dashboard: http://localhost:8085/hystrix.stream
		 */
		HystrixMetricsStreamServlet streamServlet = new HystrixMetricsStreamServlet();
		ServletRegistrationBean registrationBean = new ServletRegistrationBean(streamServlet);
		registrationBean.setLoadOnStartup(1);
		registrationBean.addUrlMappings("/hystrix.stream");
		registrationBean.setName("HystrixMetricsStreamServlet");
		return registrationBean;
	}

}