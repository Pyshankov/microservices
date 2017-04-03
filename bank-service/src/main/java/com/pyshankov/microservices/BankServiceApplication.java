package com.pyshankov.microservices;

import com.pyshankov.microservices.circuit.breaker.CircuitBreakerDefaultMethod;
import com.pyshankov.microservices.circuit.breaker.EnableCircuitBreaker;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@RestController
@SpringBootApplication
@EnableEurekaClient
@EnableFeignClients
public class BankServiceApplication {

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private BookClient bookClient;

    @RequestMapping(value = "/account/{userName}")
    public ResponseEntity<String> available(HttpServletRequest request, @PathVariable String userName) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(new URI(request.getRequestURL().toString()));
        JSONObject result = new JSONObject();
        String host = discoveryClient.getLocalServiceInstance().getHost();
        Integer port = discoveryClient.getLocalServiceInstance().getPort();
        Object res = bookClient.available(userName);
        result.put("userName", userName);
        result.put("books", res);
        result.put("node", host + ":" + port);
        return new ResponseEntity<String>(result.toJSONString(), headers, HttpStatus.OK);
    }


    public interface BookClient {

        @EnableCircuitBreaker
        Object available(String userName);

    }

    @Component
    public class BookClientService implements BookClient {

        RestTemplate restTemplate;

        @Autowired
        public BookClientService(RestTemplate restTemplate) {
            this.restTemplate = restTemplate;
        }

        @Override
        public Object available(String userName) {
            String url = "http://127.0.0.1:9001/available/" + userName;
            Object res = restTemplate.getForEntity(url, Object.class);
            return res;
        }

        @CircuitBreakerDefaultMethod
        public Object defautMethod() {
            return "CircuitBreaker is enabled";
        }
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(BankServiceApplication.class, args);
    }
}
