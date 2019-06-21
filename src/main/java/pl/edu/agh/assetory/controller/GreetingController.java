package pl.edu.agh.assetory.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import pl.edu.agh.assetory.service.GreetingService;

@Api(value = "Greeting", description = "This is sample greeting controller description")
@Controller
public class GreetingController {

    private final GreetingService service;

    public GreetingController(GreetingService service) {
        this.service = service;
    }

    @GetMapping("/greeting")
    @ApiOperation(value = "Sample short description", notes = "What it does")
    public @ResponseBody String greeting() {
        return service.greet();
    }
}
