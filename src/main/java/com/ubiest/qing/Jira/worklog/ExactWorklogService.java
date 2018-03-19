package com.ubiest.qing.Jira.worklog;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import com.ubiest.qing.Jira.entity.Worklog;

import lombok.Data;
@Data
public class ExactWorklogService implements WorklogService{

	private Map<LocalDate, Integer> worklogHours;
	
	public ExactWorklogService () {
		worklogHours = new HashMap<LocalDate, Integer>();
	}
	
	@Override
	public void addWorklog(Worklog log) {
		LocalDate date = log.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		Integer spentSeconds = log.getTimeSpentSeconds();
		
		if (worklogHours.containsKey(date)) {
			spentSeconds = log.getTimeSpentSeconds() + worklogHours.get(date);
		}
		
		worklogHours.put(date, spentSeconds);
	}
}
