package com.ubiest.qing.Jira.http;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.ubiest.qing.Jira.entity.Project;
import com.ubiest.qing.Jira.entity.User;
import com.ubiest.qing.Jira.entity.Worklog;
import com.ubiest.qing.Jira.exception.JiraHttpException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JiraHttpClient {
	
	private static final String WORKLOG_URL = "https://jiraubiest.atlassian.net/rest/tempo-timesheets/3/worklogs?";
	private static final String PROJECT_URL = "https://jiraubiest.atlassian.net/rest/api/2/project/";
	
	private static final int REQUEST_TIMEOUT = 20000;
	
	private User user;
	
	private RestTemplate restTemplate;
	
	public JiraHttpClient(User user) {
		this.user = user;
		setupRestTemplate(user, REQUEST_TIMEOUT);
	}
	
	private void setupRestTemplate(User user, int requestTimeout) {
		log.info("Set up rest template for user {}", user);
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(requestTimeout);
		factory.setReadTimeout(requestTimeout);
		restTemplate = new RestTemplate(factory);
		restTemplate.getInterceptors()
			.add(new BasicAuthorizationInterceptor(user.getUsername(), user.getPassword()));
	}
	
	public List<Worklog> retrieveWorklogsBetween(LocalDate from, LocalDate to) throws JiraHttpException {
		log.info("Search date from " + from.toString() + " to " + to.toString());
		if (from.isBefore(to)) {
			String url = generateWorklogUrl(from, to, this.user.getUsername());
			log.info("Url: " + url);
			ResponseEntity<Worklog[]> response = restTemplate.getForEntity(url, Worklog[].class);
			return Arrays.asList(response.getBody());
		} else {
			throw new JiraHttpException("Date error from " + from.toString() + " to " + to.toString());
		}
	}

	private String generateWorklogUrl(LocalDate from, LocalDate to, String username) {
		MultiValueMap<String, String> urlParams = new LinkedMultiValueMap<>();
		urlParams.add("dateFrom", from.toString());
		urlParams.add("dateTo", to.toString());
		urlParams.add("username", username);
		
		return UriComponentsBuilder.fromHttpUrl(WORKLOG_URL).queryParams(urlParams).build().toString();
	}
	
	public Project retrieveProjectById(long projectId) {
		log.info("Search project by id " + projectId);
		String url = generateProjectUrl(projectId);
		log.info("Url: " + url);
		ResponseEntity<Project> response = restTemplate.getForEntity(url, Project.class);
		return response.getBody();
	}

	private String generateProjectUrl(long projectId) {
		String url = PROJECT_URL + projectId;
		return UriComponentsBuilder.fromHttpUrl(url).build().toString();
	}
}
