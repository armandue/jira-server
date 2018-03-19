package com.ubiest.qing.Jira.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ubiest.qing.Jira.entity.User;
import com.ubiest.qing.Jira.entity.Worklog;
import com.ubiest.qing.Jira.excel.ExcelService;
import com.ubiest.qing.Jira.exception.JiraHttpException;
import com.ubiest.qing.Jira.http.JiraHttpClient;
import com.ubiest.qing.Jira.worklog.WorklogFactory;
import com.ubiest.qing.Jira.worklog.WorklogService;
import com.ubiest.qing.Jira.worklog.enums.WorklogModel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class WorklogController {
	
	@Autowired
	private ExcelService excelService;
	
	@Autowired
	private WorklogFactory worklogFactory;
	
	@RequestMapping(method=RequestMethod.POST, value="worklogfile")
	public void getWorklogExcel(
			@RequestParam(value="from") @DateTimeFormat(iso = ISO.DATE) LocalDate from,
			@RequestParam(value="to", required=false) @DateTimeFormat(iso = ISO.DATE) LocalDate to,
			@RequestBody(required=true) User user,
			@RequestParam WorklogModel worklogModel,
			HttpServletResponse response) throws IOException {
		
		log.info("Generate worklog file for {} from {} to {} with model {}", user.getUsername(), from, to, worklogModel);
		String fileName = user.getUsername() + "-" + from.getMonth().toString() + ".xlsx";
		
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-disposition", "attachment; filename=" + fileName);
		
		WorklogService logResource = worklogFactory.getWorklogResource(worklogModel);
		JiraHttpClient client = new JiraHttpClient(user);
		
		List<Worklog> logs = new ArrayList<>();
		
		try {
			
			if(to == null) {
				to = LocalDate.now();
			}
			
			logs = client.retrieveWorklogsBetween(from, to);
		} catch (JiraHttpException e) {
			e.printStackTrace();
		}
		
		for (Worklog log : logs) {
			logResource.addWorklog(log);
		}
		
		Workbook workbook = excelService.createWorklogExcelFile(logResource, user, from, to);
		workbook.write(response.getOutputStream());
	}
	
	@RequestMapping(method=RequestMethod.POST, value="worklog")
	public Map<LocalDate, Integer> getWorklogs(
			@RequestParam(value="from") @DateTimeFormat(iso = ISO.DATE) LocalDate from,
			@RequestParam(value="to", required=false) @DateTimeFormat(iso = ISO.DATE) LocalDate to,
			@RequestBody(required=true) User user) throws IOException {
		
		log.info("Retreive worklogs for {} from {} to {}", user.getUsername(), from, to);
		WorklogService logResource = worklogFactory.getWorklogResource(WorklogModel.EXACT);
		JiraHttpClient client = new JiraHttpClient(user);
		
		List<Worklog> logs = new ArrayList<>();
		
		try {
			
			if(to == null) {
				to = LocalDate.now();
			}
			
			logs = client.retrieveWorklogsBetween(from, to);
		} catch (JiraHttpException e) {
			e.printStackTrace();
		}
		
		for (Worklog log : logs) {
			logResource.addWorklog(log);
		}
		
		return logResource.getWorklogHours();
	}
}
