package com.fintech.cryptotrading.service;

import com.fintech.cryptotrading.model.User;

import java.util.Optional;

public interface LoginService {
    Optional<User> authenticate(String username, String password);
}
