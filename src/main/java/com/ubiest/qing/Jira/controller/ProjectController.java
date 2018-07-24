package com.ubiest.qing.Jira.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ubiest.qing.Jira.exception.JiraHttpException;
import com.ubiest.qing.Jira.http.JiraHttpClient;
import com.ubiest.qing.Jira.resource.ProjectResource;
import com.ubiest.qing.Jira.service.AccountService;
import com.ubiest.qing.Jira.service.ProjectService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/projects")
public class ProjectController {
	
	@Autowired
	private ProjectService projectService;
	
	@Autowired
	AccountService accountService;
	
	@RequestMapping(method=RequestMethod.POST)
	public List<ProjectResource> analyzeWorklogData(
			@RequestParam(value="from") @DateTimeFormat(iso = ISO.DATE) LocalDate from,
			@RequestParam(value="to", required=false) @DateTimeFormat(iso = ISO.DATE) LocalDate to,
			@RequestParam(required=false) String username,
			@RequestParam(required=false) String bearerToken,
			HttpServletResponse response) throws IOException, JiraHttpException {
		log.info("Retreive project resources for {} from {} to {}", username, from, to);

		if (Objects.isNull(username)) {
			username = accountService.createTestUsername();
			bearerToken = accountService.createTestBearToken();
		}
			
		JiraHttpClient client = new JiraHttpClient(username, bearerToken);
		
		if(to == null) {
			to = LocalDate.now();
		}
		
		List<ProjectResource> projectResources = projectService.retrieveProjectResourcesFrom(client, from, to);
        
		return projectResources;
	}
}
