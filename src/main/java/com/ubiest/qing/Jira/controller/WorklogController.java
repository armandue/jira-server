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
@CrossOrigin
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
			HttpServletResponse response) throws IOException, JiraHttpException {
		
		log.info("Generate worklog file for {} from {} to {} with model {}",
				user.getUsername(), from, to, worklogModel);
		
		response = configureExcel(from, user, response);
		
		WorklogService worklogService = worklogFactory.getWorklogResource(worklogModel);
		JiraHttpClient client = new JiraHttpClient(user);
			
		if(to == null) {
			to = LocalDate.now();
		}
		
		List<Worklog> logs = client.retrieveWorklogsBetween(from, to);
		
		worklogService.addWorklogs(logs);

		Workbook workbook = excelService.createWorklogExcelFile(worklogService, user, from, to);
		workbook.write(response.getOutputStream());
	}

	@RequestMapping(method=RequestMethod.POST, value="worklog")
	public Map<LocalDate, Integer> getWorklogs(
			@RequestParam(value="from") @DateTimeFormat(iso = ISO.DATE) LocalDate from,
			@RequestParam(value="to", required=false) @DateTimeFormat(iso = ISO.DATE) LocalDate to,
			@RequestBody(required=true) User user) throws IOException, JiraHttpException {
		
		log.info("Retreive worklogs for {} from {} to {}", user.getUsername(), from, to);
		WorklogService worklogService = worklogFactory.getWorklogResource(WorklogModel.EXACT);
		JiraHttpClient client = new JiraHttpClient(user);
		
		if(to == null) {
			to = LocalDate.now();
		}
		
		List<Worklog> logs = client.retrieveWorklogsBetween(from, to);
		
		worklogService.addWorklogs(logs);
		
		return worklogService.getWorklogHours();
	}
	
	private HttpServletResponse configureExcel(LocalDate startDate, User user, HttpServletResponse response) {
		String fileName = user.getUsername() + "-" + startDate.getMonth().toString() + ".xlsx";
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-disposition", "attachment; filename=" + fileName);
		return response;
	}
}
