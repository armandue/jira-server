package com.ubiest.qing.Jira.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Backdoor {
	@RequestMapping("/ping")
	public String getPong() {
		return "Qing is great.";
	}
	
	@RequestMapping("/version")
	public String version() {
		return "2.6";
	}
}
