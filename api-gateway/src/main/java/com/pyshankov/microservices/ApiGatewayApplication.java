package com.pyshankov.microservices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@EnableZuulProxy
@SpringBootApplication
@EnableEurekaClient
public class ApiGatewayApplication {

    @Autowired
    private DiscoveryClient discoveryClient;

    @RequestMapping(value = "server/{id}")
    public List<ServiceInstance> availableServers(@PathVariable String id) {
        return discoveryClient.getInstances(id);
    }

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }
}
