package com.pyshankov.microservices.circuit.breaker;

import com.pyshankov.microservices.BankServiceApplication;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.stereotype.Component;

/**
 * Created by pyshankov on 4/3/17.
 */
@Component
public class CircuitBreakerBPP implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object o, String s) throws BeansException {
        return o;
    }

    @Override
    public Object postProcessAfterInitialization(Object o, String s) throws BeansException {
        if (o instanceof BankServiceApplication.BookClientService) {
            o = Enhancer.create(BankServiceApplication.BookClient.class, new CircuitBreakerInvocationHandler(o, 5000));
        }
        return o;
    }
}
