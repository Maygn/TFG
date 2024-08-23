package app1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:application-app1.properties")
public class app1Main {
    public static void main(String[] args) {
        SpringApplication.run(app1Main.class, args);
    }
}