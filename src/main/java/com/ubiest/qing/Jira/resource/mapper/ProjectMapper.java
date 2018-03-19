package com.ubiest.qing.Jira.resource.mapper;

import org.springframework.stereotype.Service;

import com.ubiest.qing.Jira.entity.Project;
import com.ubiest.qing.Jira.resource.ProjectResource;

@Service
public class ProjectMapper implements ResourceMapper<ProjectResource, Project>{

	@Override
	public ProjectResource toResource(Project object) {
		ProjectResource projectResource = new ProjectResource();
		projectResource.setId(object.getId());
		projectResource.setKey(object.getKey());
		projectResource.setName(object.getName());
		projectResource.setDescritption(object.getDescription());
		return projectResource;
	}
}
