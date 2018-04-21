package com.ubiest.qing.Jira.worklog;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.ubiest.qing.Jira.entity.Worklog;

public interface WorklogService {
	public void addWorklog(Worklog worklog);
	public Map<LocalDate, Integer> getWorklogHours();
	
	public default void addWorklogs(List<Worklog> worklogs) {
		worklogs.forEach(this::addWorklog);
	}
}
