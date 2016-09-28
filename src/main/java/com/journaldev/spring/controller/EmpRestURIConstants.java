package com.journaldev.spring.controller;

public class EmpRestURIConstants {

	public static final String HEALTH = "/";
	public static final String DUMMY_EMP = "/rest/emp/dummy";
	public static final String GET_EMP_PARTIAL="/rest/emp/partial";
	public static final String GET_EMP = "/rest/emp/{id}";
	public static final String GET_ALL_EMP = "/rest/emps";
	public static final String GET_QUOTE = "/rest/quote";
	public static final String GET_QUOTE_WITH_DEFFERED_RESULT = "/rest/quote_with_deffered_result";
	public static final String GET_QUOTE_WITH_FUTURE_AND_CALLABLE = "/rest/quote_with_future_and_callable";
	public static final String CREATE_EMP = "/rest/emp/create";
	public static final String CREATE_EMP_FROM_STRING = "/rest/emp/create_from_string";
	public static final String CREATE_EMP_CONSUMING_JSON = "/rest/emp/create_consuming_json";
	public static final String CREATE_EMP_ASYNCHRONOUSLY = "/rest/emp/create_asynchronously";
	public static final String DELETE_EMP = "/rest/emp/delete/{id}";
}
