package com.ubiest.qing.Jira.exception;

public class JiraHttpException extends Exception{

	private static final long serialVersionUID = 1898028455527304484L;
	
	public JiraHttpException(String message) {
		super(message);
	}
}
