package com.pyshankov.microservices.circuit.breaker;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by pyshankov on 4/3/17.
 */
public class CircuitBreakerInvocationHandler implements MethodInterceptor {

    CircuitBreaker circuitBreaker;

    Object target;

    Method defaultMethod;


    public CircuitBreakerInvocationHandler(Object target, long timeCircuitBreakerEnabled) {
        circuitBreaker = new CircuitBreaker(timeCircuitBreakerEnabled);
        this.target = target;
        defaultMethod = getMethodsAnnotatedWith(target.getClass(), CircuitBreakerDefaultMethod.class).get(0);
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {

        if (method.isAnnotationPresent(EnableCircuitBreaker.class)) {
            try {
                if (!circuitBreaker.isEnabled()) {
                    Object res = methodProxy.invoke(target, objects);
                    return res;
                } else {
                    return defaultMethod.invoke(target);
                }
            } catch (Exception e) {
                circuitBreaker.setEnabled(true);
                return defaultMethod.invoke(target);
            }
        }
        return methodProxy.invoke(target, objects);
    }


    public static List<Method> getMethodsAnnotatedWith(final Class<?> type, final Class<? extends Annotation> annotation) {
        final List<Method> methods = new ArrayList<Method>();
        Class<?> klass = type;
        while (klass != Object.class) { // need to iterated thought hierarchy in order to retrieve methods from above the current instance
            // iterate though the list of methods declared in the class represented by klass variable, and add those annotated with the specified annotation
            final List<Method> allMethods = new ArrayList<Method>(Arrays.asList(klass.getDeclaredMethods()));
            for (final Method method : allMethods) {
                if (method.isAnnotationPresent(annotation)) {
                    Annotation annotInstance = method.getAnnotation(annotation);
                    // TODO process annotInstance
                    methods.add(method);
                }
            }
            // move to the upper class in the hierarchy in search for more methods
            klass = klass.getSuperclass();
        }
        return methods;
    }
}
