package com.ubiest.qing.Jira.resource.mapper;

import java.util.List;
import java.util.stream.Collectors;

public interface ResourceMapper<R, O> {
	public R toResource(O object);
	
	public default List<R> toResources(List<O> objects) {
		return objects.stream().map(o -> toResource(o)).collect(Collectors.toList());
	}
}
