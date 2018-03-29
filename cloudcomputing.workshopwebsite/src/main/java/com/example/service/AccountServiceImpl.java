package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.dao.AccountDAO;
import com.example.models.Account;


@Service("accountService")
@Transactional
public class AccountServiceImpl implements AccountService {

	@Autowired
	private AccountDAO accountDAO;
	
	@Override
	public Account findByUsernameAndPassword(String username, String password) {
		return accountDAO.findByUsernameAndPassword(username, password);
	}

}
