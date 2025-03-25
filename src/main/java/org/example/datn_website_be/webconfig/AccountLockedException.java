package org.example.datn_website_be.webconfig;


import org.springframework.security.core.AuthenticationException;

public class AccountLockedException extends AuthenticationException {

    public AccountLockedException(String message) {
        super(message);
    }
}
