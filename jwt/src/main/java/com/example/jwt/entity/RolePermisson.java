package com.example.jwt.entity;

import javax.persistence.*;

@Entity
@Table(name = "permission")
public class RolePermisson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String url;

    @Column(name = "roleName")
    private String roleName;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
