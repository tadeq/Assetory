package pl.edu.agh.assetory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:application.properties")
public class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);


    public static void main(String[] args) {
        log.info("Starting app");
        SpringApplication.run(App.class, args);
    }

}