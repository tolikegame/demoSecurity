package com.example.demo.exception;

public class NotLoginException extends RuntimeException {
    public NotLoginException(){super("請登入");}
}
