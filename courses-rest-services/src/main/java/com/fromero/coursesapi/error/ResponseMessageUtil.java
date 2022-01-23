package com.fromero.coursesapi.error;

import java.util.Objects;

import org.springframework.http.ResponseEntity;

public class ResponseMessageUtil {

	public static ResponseEntity<Object> from(ApiReturnMessage message, Object...args) {
		if (Objects.isNull(args) || args.length == 0) {
			return new ResponseEntity<>(message, message.getHttpStatus());
		}
		return new ResponseEntity<Object>(CustomReturnMessage.create(message, args), message.getHttpStatus());
	}
}
