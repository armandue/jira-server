package com.ubiest.qing.Jira.resource;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProjectResource {
	
	private Long id;
	
	private String key;
	
	private String descritption;
	
	private String name;
	
	private List<IssueResource> issues = new ArrayList<>();
	
	public void addIssueResource(IssueResource issue) {
		this.issues.add(issue);
	}
}
