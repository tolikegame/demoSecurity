package com.example.jwt.entity;

import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;

import java.util.List;

@Entity
@Table(name = "user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "user")
    private List<Role> authorities;

    @Override
    public List<Role> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<Role> authorities) {
        this.authorities = authorities;
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    //用戶帳號是否過期
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    //用戶帳號是否被鎖定
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    //用戶密碼是否過期
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //用戶是否可用
    @Override
    public boolean isEnabled() {
        return true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
