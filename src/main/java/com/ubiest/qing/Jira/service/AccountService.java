package com.ubiest.qing.Jira.service;

import org.springframework.stereotype.Service;

@Service
public class AccountService {
	
	public String createTestUsername() {
		return "test.username";
	}
	
	public String createTestBearToken() {
		return "test.bearToken";
	}
}
