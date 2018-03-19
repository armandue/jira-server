package com.ubiest.qing.Jira.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties
public class Project {
	@JsonProperty("id")
	private Long id;
	
	@JsonProperty("key")
	private String key;
	
	@JsonProperty("description")
	private String description;
	
	@JsonProperty("name")
	private String name;
}
