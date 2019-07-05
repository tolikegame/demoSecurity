package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CommonController {

    @GetMapping("/common")
    public String common(){
        return "一般訪問測試";
    }

    @GetMapping("/common/dir")
    public String dir(){
        return "瀏覽資料";
    }

    @GetMapping("/sessionError")
    public String sessionError(){

        return "連線失敗";
    }

    @GetMapping("/")
    public String demo()  {
        return "已登入";
    }

    @GetMapping(value = "/logout")
    public String logout(){
        return "已登出";
    }

}
