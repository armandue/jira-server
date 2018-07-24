package com.ubiest.qing.Jira.http;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.ubiest.qing.Jira.entity.Project;
import com.ubiest.qing.Jira.entity.Worklog;
import com.ubiest.qing.Jira.exception.JiraHttpException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JiraHttpClient {
	
	private static final String WORKLOG_URL = "https://api.tempo.io/rest-legacy/tempo-timesheets/3/worklogs?";
	private static final String PROJECT_URL = "https://jiraubiest.atlassian.net/rest/api/2/project/";
	
	private static final int REQUEST_TIMEOUT = 20000;
	
	private String username;
	
	private String bearerToken;
	
	private RestTemplate restTemplate;
	
	private HttpEntity<String> bearerAuthEntity;
	
	private HttpEntity<String> basicAuthEntity;
	
	public JiraHttpClient(String username, String bearerToken) {
		this.username = username;
		this.bearerToken = bearerToken;
		this.bearerAuthEntity = createBearerAuth();
		this.basicAuthEntity = createBasicAuth();
		setupRestTemplate();
	}
	
	public List<Worklog> retrieveWorklogsBetween(LocalDate from, LocalDate to) throws JiraHttpException {
		
		log.info("Search date from " + from.toString() + " to " + to.toString());
		if (from.isBefore(to)) {
			String url = generateWorklogUrl(from, to, username);
			log.info("Url: " + url);
			ResponseEntity<Worklog[]> response = restTemplate.exchange(
					url,
					HttpMethod.GET, 
					bearerAuthEntity, 
					Worklog[].class);
			
			return Arrays.asList(response.getBody());
		} else {
			throw new JiraHttpException("Date error from " + from.toString() + " to " + to.toString());
		}
	}

	private HttpEntity<String> createBearerAuth() {
		log.info("Create authentication with {} and {}.", username, bearerToken);
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + bearerToken);
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		return entity;
	}
	
	private HttpEntity<String> createBasicAuth() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + "test");
		HttpEntity<String> entity = new HttpEntity<>(null, headers);
		return entity;
	}
	
	public Project retrieveProjectById(long projectId) {
		
		log.info("Search project by id " + projectId);
		String url = generateProjectUrl(projectId);
		log.info("Url: " + url);
		ResponseEntity<Project> response = restTemplate.exchange(
				url, 
				HttpMethod.GET, 
				basicAuthEntity, 
				Project.class);
		return response.getBody();
	}
	
	private String generateWorklogUrl(LocalDate from, LocalDate to, String username) {
		MultiValueMap<String, String> urlParams = new LinkedMultiValueMap<>();
		urlParams.add("dateFrom", from.toString());
		urlParams.add("dateTo", to.toString());
		urlParams.add("username", username);
		
		return UriComponentsBuilder.fromHttpUrl(WORKLOG_URL).queryParams(urlParams).build().toString();
	}

	private String generateProjectUrl(long projectId) {
		String url = PROJECT_URL + projectId;
		return UriComponentsBuilder.fromHttpUrl(url).build().toString();
	}
	
	private void setupRestTemplate() {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(REQUEST_TIMEOUT);
		factory.setReadTimeout(REQUEST_TIMEOUT);
		restTemplate = new RestTemplate(factory);
	}
}
