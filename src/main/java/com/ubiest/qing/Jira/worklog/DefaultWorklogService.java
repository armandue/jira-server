package com.ubiest.qing.Jira.worklog;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import com.ubiest.qing.Jira.entity.Worklog;

import lombok.Data;
@Data
public class DefaultWorklogService implements WorklogService{
	
	public static final int DEFAULT_WORK_HOUR = 8 * 60 * 60;
	
	private Map<LocalDate, Integer> worklogHours;
	
	public DefaultWorklogService () {
		worklogHours = new HashMap<LocalDate, Integer>();
	}
	
	@Override
	public void addWorklog(Worklog log) {
		LocalDate date = log.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		if (!worklogHours.containsKey(date)) {
			worklogHours.put(date, DEFAULT_WORK_HOUR);
		}
	}
}
