package com.tymoshenko.controller;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Yakiv Tymoshenko
 * @since 29.01.2016
 */
@Configuration
@ComponentScan("com.tymoshenko")
public class Application {

    public static void main(String[] args) {
        org.springframework.context.ApplicationContext ctx = new AnnotationConfigApplicationContext(Application.class);
        TaskManager taskManager = ctx.getBean(TaskManager.class);
        taskManager.taskList();
    }
}
