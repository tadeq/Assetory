package pl.edu.agh.assetory.service;

import org.springframework.stereotype.Service;

@Service
public class GreetingService {
    private static final String HELLO_WORLD = "Hello World";

    public String greet() {
        return HELLO_WORLD;
    }
}
