package com.ubiest.qing.Jira.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/backdoor")
public class Backdoor {
	
	@RequestMapping("/ping")
	public String getPong() {
		return "Qing is great.";
	}
	
	@RequestMapping("/version")
	public String version() {
		return "3.0";
	}
}
