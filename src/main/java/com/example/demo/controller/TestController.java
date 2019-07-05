package com.example.demo.controller;

import com.example.demo.exception.NotLoginException;
import com.example.demo.model.po.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.naming.Context;
import java.util.List;

@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
@RestController
public class TestController {



    @GetMapping("/hello")
    public String hello(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return "Hello\t"+ authentication.getName();
    }


    @GetMapping(value = "/test")
    public String say()throws NotLoginException{
        return "測試登入";
    }




    @Autowired
    private SessionRegistry sessionRegistry;

    @GetMapping(value = "/session")
    public List<Object> principals(){
        return sessionRegistry.getAllPrincipals();
    }

    @GetMapping(value = "/deleteSession")
    public Object deleteSession(@RequestParam String account){
        List<Object> users = sessionRegistry.getAllPrincipals(); // 获取session中所有的用户信息
        for (Object principal : users) {
            if(principal instanceof Users){
                return "qwq0";
            }
                if (account.equals(principal)) {
                    List<SessionInformation> sessionsInfo = sessionRegistry.getAllSessions(principal, false); // false代表不包含过期session
                    if (null != sessionsInfo && sessionsInfo.size() > 0) {
                        for (SessionInformation sessionInformation : sessionsInfo) {
                            sessionInformation.expireNow();
//                            sessionRegistry.removeSessionInformation();
                        }
                    }
            }
        }
        return "操作成功";
    }
}
