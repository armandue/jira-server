package com.ubiest.qing.Jira.resource.mapper;

import org.springframework.stereotype.Service;

import com.ubiest.qing.Jira.entity.Worklog;
import com.ubiest.qing.Jira.resource.WorklogResource;

@Service
public class WorklogMapper implements ResourceMapper<WorklogResource, Worklog>{

	@Override
	public WorklogResource toResource(Worklog object) {
		WorklogResource worklogResource = new WorklogResource();
		worklogResource.setStartDate(object.getStartDate());
		worklogResource.setTimeSpentSeconds(object.getTimeSpentSeconds());
		return worklogResource;
	}

}
