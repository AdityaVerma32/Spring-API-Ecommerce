package com.project.e_commerce.Controller;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @GetMapping("/Students")
    public String getter(){
        return "Hello World";
    }

}
