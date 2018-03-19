package com.ubiest.qing.Jira.entity;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties
public class Issue {
	
	@JsonProperty("key")
	private String key;
	
	@JsonProperty("id")
	private Long id;
	
	@JsonProperty("projectId")
	private Long projectId;
	
	private String type;
	
	@JsonProperty("issueType")
	private void unpackIssueType(Map<String, Object> issueTypeObj) {
		this.type = (String) issueTypeObj.get("name");
	}
}
