package org.mickey.billing;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

@MapperScan("org.mickey.billing.mapper")
@SpringBootApplication
public class BillingMain {

    public static ConfigurableEnvironment environment;

    public static void main(String[] args) {
        ConfigurableApplicationContext configurableApplicationContext = SpringApplication.run(BillingMain.class, args);
        environment = configurableApplicationContext.getEnvironment();
    }
}