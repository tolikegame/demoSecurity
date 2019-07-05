package com.example.demo.model.po;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "users")
public class Users  {

    @Id
    private String account;
    @Column(name = "password")
    private String password;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//        Users users = (Users) o;
//        return Objects.equals(account, users.account);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(account);
//    }
}
