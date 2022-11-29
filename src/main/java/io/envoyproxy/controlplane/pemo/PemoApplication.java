package io.envoyproxy.controlplane.pemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@SpringBootApplication
public class PemoApplication {

    @Autowired
    private ApplicationContext appContext;


    public static void main(String[] args) {

        SpringApplication.run(PemoApplication.class, args);

    }


    @Bean
    public CommandLineRunner run(ApplicationContext appContext) {
        return args -> {

            String[] beans = appContext.getBeanDefinitionNames();
            System.out.println("\033[33m");
            Arrays.stream(beans).sorted().forEach(System.out::println);
            System.out.println("\033[0m");
        };
    }
}
