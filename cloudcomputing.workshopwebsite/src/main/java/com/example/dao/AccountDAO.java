package com.example.dao;

import org.springframework.data.repository.CrudRepository;

import com.example.models.Account;

public interface AccountDAO extends CrudRepository<Account, Integer> {
	public Account findByUsernameAndPassword(String username, String password);
}
