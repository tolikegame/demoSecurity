package com.example.demo.controller;

import com.example.demo.model.request.UserRequest;
import com.example.demo.model.po.Users;
import com.example.demo.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class UserController {

    @Autowired
    UsersRepository usersRepository;

    PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    PasswordEncoder encoder2 = new BCryptPasswordEncoder();

    @PostMapping(value = "/save")
    public String saveUser(@RequestBody UserRequest model) {
        Users checkUser = usersRepository.findByAccount(model.getAccount());
        if(null!=checkUser){
            return "帳戶已存在";
        }

        Users user = new Users();
//        long UserCount =usersRepository.count();
//        int addId = (int) (UserCount+1);
//        users.setId(addId);
        user.setAccount(model.getAccount());

        //加密
        String encrypt = PasswordEncoderFactories.createDelegatingPasswordEncoder().encode(model.getPassword());
//        String bcPwd = encoder2.encode(model.getPassword());
        //md5加密
//        MyPasswordEncoder encode = new MyPasswordEncoder();
//        String pwd = encode.encode(model.getPassword());

        user.setPassword(encoder.encode(model.getPassword()));
        Users data = usersRepository.save(user);
        if (null == data) {
            return "註冊失敗";
        }
        return "註冊完成";
    }
    @DeleteMapping(value = "/delete")
    public String deleteUser(String account) {
        Users users = new Users();
        users.setAccount(account);
        try {
            usersRepository.delete(users);
        } catch (Exception e) {
            e.printStackTrace();
            return "刪除失敗";
        }
        return "刪除完成";
    }

    @PutMapping(value = "/update")
    public String updateUser(@RequestBody UserRequest model){
        Users checkUser = usersRepository.findByAccount(model.getAccount());
        Users user = new Users();
        user.setPassword(model.getPassword());
        try {
            usersRepository.save(user);
        } catch (Exception e) {
            e.printStackTrace();
            return "修改失敗";
        }
        return "修改完成";
    }

    @PostMapping(value = "/findOne")
    public Users findOneUser(@RequestBody UserRequest model){
        return usersRepository.findByAccount(model.getAccount());
    }

    @PostMapping(value = "/findAll")
//    @PreAuthorize("principal.username.equals(#username)")
    public List<Users> findAllUser(@RequestBody UserRequest model){
        List<Users> list = new ArrayList<>();
        List<Users> users = usersRepository.findAll();
        for (Users user : users){
            list.add(user);
        }
        return list;
    }
}
