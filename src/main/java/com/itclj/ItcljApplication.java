package com.itclj;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages={"com.itclj"})
public class ItcljApplication {

    public static void main(String[] args) {
        SpringApplication.run(ItcljApplication.class,args);
    }

}
