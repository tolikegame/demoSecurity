package com.example.demo.security;

import com.example.demo.model.po.Users;
import com.example.demo.repository.UsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class CustomProvider implements AuthenticationProvider {

    Logger logger = LoggerFactory.getLogger(CustomProvider.class);

    PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
//    MyPasswordEncoder encode = new MyPasswordEncoder();

    @Autowired
    UsersRepository usersRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String account = authentication.getPrincipal().toString();
        String password = authentication.getCredentials().toString();
        Users users = usersRepository.findByAccount(account);


        if(password == null || !passwordEncoder.matches(password,users.getPassword())){
            logger.info(String.format("user %s Not found ", password));
            throw new AuthenticationServiceException(String.format("login fail account = %s ", password));
        }

        List<GrantedAuthority> grantedAuths = new ArrayList<>();
        if(users.getAccount().equals("admin")){
            grantedAuths.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        grantedAuths.add(new SimpleGrantedAuthority("ROLE_USER"));

        return new UsernamePasswordAuthenticationToken(account,password,grantedAuths);

//        String account = authentication.getName();
//        String password = authentication.getCredentials().toString();
//
//        if (authenticatedAgainstThirdPartySystem()) {
//            List<GrantedAuthority> grantedAuths = new ArrayList<>();
//            return new UsernamePasswordAuthenticationToken(account, password, grantedAuths);
//        } else {
//            return null;
//        }

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(
                UsernamePasswordAuthenticationToken.class);
    }
}
