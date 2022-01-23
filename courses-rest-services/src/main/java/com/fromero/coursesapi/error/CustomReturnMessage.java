package com.fromero.coursesapi.error;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomReturnMessage {

	private int code;
	private String message;
	private HttpStatus httpStatus;

	public static CustomReturnMessage create(ApiReturnMessage m, Object...args) {
		return new CustomReturnMessage(m, args);
	}
	
	public CustomReturnMessage(ApiReturnMessage m, Object...args) {
		code = m.getCode();
		message = String.format(m.getMessage(), args);
		httpStatus = m.getHttpStatus();
	}
}
