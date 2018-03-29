package com.example.service;

import com.example.models.Account;

public interface AccountService {
	public Account findByUsernameAndPassword(String username, String password);
}
