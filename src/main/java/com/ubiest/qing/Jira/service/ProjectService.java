package com.ubiest.qing.Jira.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ubiest.qing.Jira.entity.Issue;
import com.ubiest.qing.Jira.entity.Project;
import com.ubiest.qing.Jira.entity.Worklog;
import com.ubiest.qing.Jira.exception.JiraHttpException;
import com.ubiest.qing.Jira.http.JiraHttpClient;
import com.ubiest.qing.Jira.resource.IssueResource;
import com.ubiest.qing.Jira.resource.ProjectResource;
import com.ubiest.qing.Jira.resource.WorklogResource;
import com.ubiest.qing.Jira.resource.mapper.IssueMapper;
import com.ubiest.qing.Jira.resource.mapper.ProjectMapper;
import com.ubiest.qing.Jira.resource.mapper.WorklogMapper;

@Service
public class ProjectService {
	
	@Autowired
	private IssueMapper issueMapper;
	@Autowired
	private WorklogMapper worklogMapper;
	@Autowired
	private ProjectMapper projectMapper;
	
	private JiraHttpClient client;
	
	public List<ProjectResource> retrieveProjectResourcesFrom(JiraHttpClient client, LocalDate from, LocalDate to)
			throws JiraHttpException {
		this.client = client;
		List<Worklog> logs = client.retrieveWorklogsBetween(from, to);
		List<ProjectResource> projectResources = convertFrom(logs);
		return projectResources;
	}
	
	private List<ProjectResource> convertFrom(List<Worklog> worklogs) {
		Map<Long, Map<Issue, List<Worklog>>> issueMap = sortByProjectIdAndByIssue(worklogs);
		return convertToProjectResources(issueMap);
	}

	private List<ProjectResource> convertToProjectResources(Map<Long, Map<Issue, List<Worklog>>> issueMap) {
		
		List<ProjectResource> projectResources = new ArrayList<>();
		
		issueMap.forEach((projectId, worklogMap) -> {
			Project project = client.retrieveProjectById(projectId);
			List<IssueResource> issueResources = new ArrayList<>();
			
			worklogMap.forEach((issue, logs) -> {
				IssueResource issueResource = issueMapper.toResource(issue);
				List<WorklogResource> logResources = worklogMapper.toResources(logs);
				issueResource.setWorklogs(logResources);
				issueResources.add(issueResource);
			});
			
			ProjectResource projectResource = projectMapper.toResource(project, issueResources);
			projectResources.add(projectResource);
		});
		return projectResources;
	}

	private Map<Long, Map<Issue, List<Worklog>>> sortByProjectIdAndByIssue(List<Worklog> worklogs) {
		return worklogs.stream()
			.collect(Collectors.groupingBy(log -> log.getIssue().getProjectId(),
					Collectors.groupingBy(log -> log.getIssue())) 
				);
	}
}
