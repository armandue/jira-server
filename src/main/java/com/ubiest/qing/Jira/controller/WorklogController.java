package com.ubiest.qing.Jira.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ubiest.qing.Jira.entity.Worklog;
import com.ubiest.qing.Jira.excel.ExcelService;
import com.ubiest.qing.Jira.exception.JiraHttpException;
import com.ubiest.qing.Jira.http.JiraHttpClient;
import com.ubiest.qing.Jira.service.AccountService;
import com.ubiest.qing.Jira.worklog.WorklogFactory;
import com.ubiest.qing.Jira.worklog.WorklogHandler;
import com.ubiest.qing.Jira.worklog.enums.WorklogModel;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/worklogs")
public class WorklogController {
	
	@Autowired
	private ExcelService excelService;
	
	@Autowired
	private WorklogFactory worklogFactory;
	
	@Autowired
	AccountService accountService;
	
	@RequestMapping(method=RequestMethod.POST, value="/file")
	public void getWorklogExcel(
			@RequestParam(value="from") @DateTimeFormat(iso = ISO.DATE) LocalDate from,
			@RequestParam(value="to", required=false) @DateTimeFormat(iso = ISO.DATE) LocalDate to,
			@RequestParam(required=false) String username,
			@RequestParam(required=false) String bearerToken,
			@RequestParam WorklogModel worklogModel,
			HttpServletResponse response) throws IOException, JiraHttpException {
		
		if (worklogModel == WorklogModel.TEST) {
			username = accountService.createTestUsername();
			bearerToken = accountService.createTestBearToken();
		}
		
		log.info("Generate worklog file for {} from {} to {} with model {}",
				username, from, to, worklogModel);
		
		response = excelService.configureExcel(from, username, response);
		
		WorklogHandler worklogService = worklogFactory.getWorklogResource(worklogModel);
		JiraHttpClient client = new JiraHttpClient(username, bearerToken);
			
		if(to == null) {
			to = LocalDate.now();
		}
		
		List<Worklog> logs = client.retrieveWorklogsBetween(from, to);
		
		worklogService.addWorklogs(logs);

		Workbook workbook = excelService.createWorklogExcelFile(worklogService, username, from, to);
		workbook.write(response.getOutputStream());
	}

	@RequestMapping(method=RequestMethod.POST)
	public Map<LocalDate, Integer> getWorklogs(
			@RequestParam(value="from") @DateTimeFormat(iso = ISO.DATE) LocalDate from,
			@RequestParam(value="to", required=false) @DateTimeFormat(iso = ISO.DATE) LocalDate to,
			@RequestParam(required=false) String username,
			@RequestParam(required=false) String bearerToken) throws IOException, JiraHttpException {
		
		log.info("Retreive worklogs for {} from {} to {}", username, from, to);
		
		if (username == null || username.length() == 0) {
			username = accountService.createTestUsername();
			bearerToken = accountService.createTestBearToken();
		}
		
		WorklogHandler worklogService = worklogFactory.getWorklogResource(WorklogModel.EXACT);
		JiraHttpClient client = new JiraHttpClient(username, bearerToken);
		
		if(to == null) {
			to = LocalDate.now();
		}
		
		List<Worklog> logs = client.retrieveWorklogsBetween(from, to);
		
		worklogService.addWorklogs(logs);
		
		return worklogService.getWorklogHours();
	}
}
