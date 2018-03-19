package com.ubiest.qing.Jira.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ubiest.qing.Jira.entity.Project;
import com.ubiest.qing.Jira.entity.User;
import com.ubiest.qing.Jira.entity.Worklog;
import com.ubiest.qing.Jira.exception.JiraHttpException;
import com.ubiest.qing.Jira.http.JiraHttpClient;
import com.ubiest.qing.Jira.resource.ProjectResource;
import com.ubiest.qing.Jira.resource.mapper.ProjectMapper;
import com.ubiest.qing.Jira.service.AnalyzeService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class AnalysisController {
	
	@Autowired
	private AnalyzeService analyzeService;
	
	@Autowired
	private ProjectMapper projectMapper;
	
	@RequestMapping(method=RequestMethod.POST, value="/analyze")
	public List<ProjectResource> analyzeWorklogData(
			@RequestParam(value="from") @DateTimeFormat(iso = ISO.DATE) LocalDate from,
			@RequestParam(value="to", required=false) @DateTimeFormat(iso = ISO.DATE) LocalDate to,
			@RequestBody(required=true) User user,
			HttpServletResponse response) throws IOException {
		
		log.info("Retreive project resources for {} from {} to {}", user.getUsername(), from, to);
		
		JiraHttpClient client = new JiraHttpClient(user);
		List<Worklog> logs = new ArrayList<>();
		
		try {
			if(to == null) {
				to = LocalDate.now();
			}
			logs = client.retrieveWorklogsBetween(from, to);
		} catch (JiraHttpException e) {
			e.printStackTrace();
		}
		
		List<ProjectResource> projectResources = analyzeService.generateProjectResources(logs);
		
		return projectResources.stream().map(p -> {
			Project project = client.retrieveProjectById(p.getId());
			ProjectResource projectResource = projectMapper.toResource(project);
			projectResource.setIssues(p.getIssues());
			return projectResource;
		}).collect(Collectors.toList());
	}
}
