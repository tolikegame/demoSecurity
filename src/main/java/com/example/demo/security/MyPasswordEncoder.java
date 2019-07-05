package com.example.demo.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.DigestUtils;

public class MyPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence charSequence){
        return DigestUtils.md5DigestAsHex(charSequence.toString().getBytes());
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword){
        return encode(rawPassword).equals(encodedPassword);
    }
}
