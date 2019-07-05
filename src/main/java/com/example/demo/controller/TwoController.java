package com.example.demo.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasRole('TEST')")
public class TwoController {

    @GetMapping("/two")
    public String two(){
        return "2號設定";
    }
    @GetMapping("/tt")
    public String tt(){
        return "test";
    }
}
