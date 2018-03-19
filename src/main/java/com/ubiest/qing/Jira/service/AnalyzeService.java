package com.ubiest.qing.Jira.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ubiest.qing.Jira.entity.Issue;
import com.ubiest.qing.Jira.entity.Worklog;
import com.ubiest.qing.Jira.resource.IssueResource;
import com.ubiest.qing.Jira.resource.ProjectResource;
import com.ubiest.qing.Jira.resource.WorklogResource;
import com.ubiest.qing.Jira.resource.mapper.IssueMapper;
import com.ubiest.qing.Jira.resource.mapper.WorklogMapper;

@Service
public class AnalyzeService {
	
	@Autowired
	private IssueMapper issueMapper;
	@Autowired
	private WorklogMapper worklogMapper;
	
	public List<ProjectResource> generateProjectResources(List<Worklog> worklogs) {
		
		Map<Long, Map<Issue, List<Worklog>>> issueMap = sortWorklogsByProjectIdByIssue(worklogs);
		
		return convertToProjectResources(issueMap);
	}

	private List<ProjectResource> convertToProjectResources(Map<Long, Map<Issue, List<Worklog>>> issueMap) {
		
		List<ProjectResource> projects = new ArrayList<>();
		
		issueMap.forEach((projectId, worklogMap) -> {
			ProjectResource pr = new ProjectResource();
			pr.setId(projectId);
			
			worklogMap.forEach((issue, logs) -> {
				IssueResource issueResource = issueMapper.toResource(issue);
				List<WorklogResource> logResources = worklogMapper.toResources(logs);
				issueResource.setWorklogs(logResources);
				pr.addIssueResource(issueResource);
			});
			
			projects.add(pr);
		});
		return projects;
	}

	private Map<Long, Map<Issue, List<Worklog>>> sortWorklogsByProjectIdByIssue(List<Worklog> worklogs) {
		return worklogs.stream()
			.collect(Collectors.groupingBy(log -> log.getIssue().getProjectId(),
					Collectors.groupingBy(log -> log.getIssue())) 
				);
	}
	
}
