package com.ubiest.qing.Jira.entity;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties
public class Worklog {
	
	@JsonProperty("timeSpentSeconds")
	private int timeSpentSeconds;
	
	@JsonProperty("dateStarted")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
	private Date startDate;
	
	@JsonProperty("issue")
	private Issue issue;
}
