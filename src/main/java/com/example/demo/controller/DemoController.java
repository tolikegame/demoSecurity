package com.example.demo.controller;

import org.apache.catalina.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class DemoController {

//    @GetMapping("/")
//    public String demo(){
//        return "Hello Spring Security";
//    }

//    @GetMapping("/hello")
//    public String hello(){
//        return "Hello World";
//    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_ROOT')")
    @GetMapping("/role")
    public String role(){
        return "for admin or root";
    }

    @PreAuthorize("#id<10 and principal.username.equals(#username)")
    @GetMapping("/check_info")
    public String checkInfo(Integer id,String username){
        SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return "success";
    }

    @PreAuthorize("#user.username.equals('admin')")
    @GetMapping("/check_user")
    public String checkUser(User user) {
        return "success";
    }

}
