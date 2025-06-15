package com.lalit.noteapp.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {

    @GetMapping("/hello")
    public String demo(){
        return "Hello finally from the long day u come back";
    }
}
