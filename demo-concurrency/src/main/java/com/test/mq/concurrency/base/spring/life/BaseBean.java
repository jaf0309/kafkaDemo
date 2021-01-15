package com.test.mq.concurrency.base.spring.life;


import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;

@Component
public class BaseBean implements BeanNameAware , BeanPostProcessor, CommandLineRunner, DisposableBean, InitializingBean , ApplicationContextAware, SmartInitializingSingleton, InstantiationAwareBeanPostProcessor {
    private String name;
    /*public BaseBean(String name,Integer age){
        this.age = age;
        this.name = name;
    }*/
    public BaseBean(){
        System.out.println("1:constract");
    }

    @PostConstruct
    public void init(){
        System.out.println("4:PostConstruct");
    }

    @Override
    public void setBeanName(String name) {
        System.out.println("2:BeanNameAware");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("DisposableBean");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("5:InitializingBean");
    }

    @Override
    public void afterSingletonsInstantiated() {
        System.out.println("6SmartInitializingSingleton");
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("7CommandLineRunner");
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        System.out.println("3:ApplicationContextAware");
    }

    @Override
    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
        System.out.println("InstantiationAwareBeanPostProcessor->InstantiationAwareBeanPostProcessor");
        System.out.println(beanClass.getName()+"---------");
        return null;
    }

    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        System.out.println("InstantiationAwareBeanPostProcessor->postProcessProperties");
        Class<?> aClass = bean.getClass();
        System.out.println(aClass.getName()+"#######");
        return pvs;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("BeanPostProcessor->postProcessBeforeInitialization");
        Class<?> aClass = bean.getClass();
        System.out.println(aClass.getName()+"*****");
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        System.out.println("BeanPostProcessor->postProcessAfterInitialization");
        Class<?> aClass = bean.getClass();
        System.out.println(aClass.getName()+"=====");
        return bean;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
