package com.ubiest.qing.Jira.resource;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class IssueResource {
	
	private String key;
	
	private String type;
	
	private List<WorklogResource> worklogs;
}
