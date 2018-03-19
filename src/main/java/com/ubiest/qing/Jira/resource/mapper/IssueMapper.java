package com.ubiest.qing.Jira.resource.mapper;

import org.springframework.stereotype.Service;

import com.ubiest.qing.Jira.entity.Issue;
import com.ubiest.qing.Jira.resource.IssueResource;

@Service
public class IssueMapper implements ResourceMapper<IssueResource, Issue>{

	@Override
	public IssueResource toResource(Issue object) {
		IssueResource issueResource = new IssueResource();
		issueResource.setKey(object.getKey());
		issueResource.setType(object.getType());
		return issueResource;
	}

}
