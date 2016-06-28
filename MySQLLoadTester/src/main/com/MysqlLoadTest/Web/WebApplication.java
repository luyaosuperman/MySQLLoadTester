package com.MysqlLoadTest.Web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@ImportResource("classpath:beans.xml")
public class WebApplication {

    public static void main(String[] args) {
    	
    	System.out.println("java.class.path: " + System.getProperty("java.class.path"));
        SpringApplication.run(WebApplication.class, args);
    }
	
}
