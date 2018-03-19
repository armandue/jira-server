package com.ubiest.qing.Jira.worklog;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ubiest.qing.Jira.worklog.enums.WorklogModel;

@Service
public class WorklogFactory {
	
	private Map<WorklogModel, WorklogService> worklogServices = new HashMap<>();
	
	private void setup() {
		worklogServices.put(WorklogModel.DEFAULT, new DefaultWorklogService());
		worklogServices.put(WorklogModel.EXACT, new ExactWorklogService());
	}
	
	public WorklogService getWorklogResource(WorklogModel model) {
		setup();
		return this.worklogServices.get(model);
	}
}
