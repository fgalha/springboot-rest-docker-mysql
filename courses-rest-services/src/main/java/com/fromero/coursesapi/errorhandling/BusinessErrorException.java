package com.fromero.coursesapi.errorhandling;

import com.fromero.coursesapi.error.ApiReturnMessage;

public class BusinessErrorException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private ApiReturnMessage apiReturnMessage;
	private Object[] args;
	
	public BusinessErrorException() {
		super();
	}
	
	public BusinessErrorException(ApiReturnMessage apiReturnMessage, Object...args) {
		super();
		this.apiReturnMessage = apiReturnMessage;
		this.args = args;
	}

	public BusinessErrorException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public BusinessErrorException(String message, Throwable cause) {
		super(message, cause);
	}

	public BusinessErrorException(String message) {
		super(message);
	}

	public BusinessErrorException(Throwable cause) {
		super(cause);
	}

	public ApiReturnMessage getApiReturnMessage() {
		return apiReturnMessage;
	}

    public Object[] getArgs() {
		return args;
	}
}
