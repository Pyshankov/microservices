package com.pyshankov.microservices.userservice;

import javafx.util.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@SpringBootApplication
@EnableEurekaClient
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }


    private List<Pair<String, String>> books;

    @Autowired
    private DiscoveryClient discoveryClient;

    @RequestMapping(value = "/available/{userName}")
    public Map<String, Object> available(@PathVariable String userName) {
        Map<String, Object> res = new HashMap<>();
        String host = discoveryClient.getLocalServiceInstance().getHost();
        Integer port = discoveryClient.getLocalServiceInstance().getPort();

        res.put("books", books.stream().filter(book -> book.getKey().equals(userName)).map(book -> book.getValue()).collect(Collectors.toList()));
        res.put("node", host + ":" + port);
        return res;
    }


    @Bean
    public CommandLineRunner demo() {
        return (args) -> {
            books = new LinkedList<>();
            books.add(new Pair<>("pyshankov", "Spring in Action"));
            books.add(new Pair<>("pyshankov", "Java Concurrency in Practice"));
            books.add(new Pair<>("pyshankov", "EJB in Action"));
            books.add(new Pair<>("pyshankov", "Effective Java"));
            books.add(new Pair<>("pyshankov", "Java Persistence with Hibernate"));

            books.add(new Pair<>("alex", "Dive into Python"));
            books.add(new Pair<>("alex", "Python for Data Analysis"));
            books.add(new Pair<>("alex", "Django for Web"));
        };
    }

}
