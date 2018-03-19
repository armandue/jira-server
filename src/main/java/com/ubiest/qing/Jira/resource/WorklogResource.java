package com.ubiest.qing.Jira.resource;

import java.util.Date;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WorklogResource {
	
	private int timeSpentSeconds;
	
	private Date startDate;
}
