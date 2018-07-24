package com.ubiest.qing.Jira.worklog;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ubiest.qing.Jira.worklog.enums.WorklogModel;

@Service
public class WorklogFactory {
	
	private Map<WorklogModel, WorklogHandler> worklogHandlers = new HashMap<>();
	
	private void setup() {
		worklogHandlers.put(WorklogModel.DEFAULT, new DefaultWorklogHandler());
		worklogHandlers.put(WorklogModel.EXACT, new ExactWorklogHandler());
		worklogHandlers.put(WorklogModel.TEST, new ExactWorklogHandler());
	}
	
	public WorklogHandler getWorklogResource(WorklogModel model) {
		setup();
		return this.worklogHandlers.get(model);
	}
}
