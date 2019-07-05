package com.example.demo.repository;

import com.example.demo.model.po.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users,String> {
    Users findByAccount(String account);
}
