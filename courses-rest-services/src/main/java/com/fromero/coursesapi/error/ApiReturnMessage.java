package com.fromero.coursesapi.error;

import java.io.Serializable;

import org.springframework.http.HttpStatus;

public enum ApiReturnMessage implements Serializable {

	/* Success messages */
	STUDENT_REGISTERED(1, "Student registered. Id = %d", HttpStatus.OK),
	STUDENT_UPDATED(2, "Student data updated. Id = %d", HttpStatus.OK),
	STUDENT_DELETED(3, "Student excluded. Id = %d", HttpStatus.OK),
	STUDENT_RATING_UPDATED(4, "Student id = %d updated the rating to %d for the course id %d", HttpStatus.OK),
	
	COURSE_REGISTERED(10, "Course registered. Id = %d", HttpStatus.OK),
	COURSE_UPDATED(11, "Course data updated. Id = %d", HttpStatus.OK),
	COURSE_DELETED(12, "Course excluded. Id = %d", HttpStatus.OK),
	
	STUDENT_ENROLLED_TO_COURSE(21, "Student id = %d is enrolled to the course id = %d ", HttpStatus.OK),
	STUDENT_UNREGISTER_TO_COURSE(22, "Student id = %d is unregistered to the course id = %d ", HttpStatus.OK),
	
	/* Error messages */
	ERR_STUDENT_NOT_FOUND(10000, "Student id = %d not found", HttpStatus.NOT_FOUND),
	ERR_COURSE_NOT_FOUND(10001, "Course id = %d not found", HttpStatus.NOT_FOUND),
	ERR_MAXIMUM_COURSES_BY_STUDENT_REACHED(10002, "The student id = %d reached the maximum of %d courses.", HttpStatus.BAD_REQUEST),
	ERR_MAXIMUM_STUDENTS_BY_COURSE_REACHED(10003, "The course id = %d reached the maximum of %d students.", HttpStatus.BAD_REQUEST),
	ERR_STUDENT_ALREADY_ENROLLED(10004, "Student id = %d is already enrolled to the course id = %d", HttpStatus.BAD_REQUEST),
	ERR_STUDENT_IS_NOT_ENROLLED(10005, "Student id = %d is not enrolled to the course id = %d", HttpStatus.BAD_REQUEST),
	ERR_RATING_OUT_OF_RANGE(10006, "Rating = %d is out of the range (%d,%d)", HttpStatus.BAD_REQUEST),
	ERR_ID_NOT_INFORMED(10007," Id not informed", HttpStatus.BAD_REQUEST)
	;
	
	private int code;
	private String message;
	private HttpStatus httpStatus;
	
	private ApiReturnMessage(int code, String message, HttpStatus httpStatus) {
		this.code = code;
		this.message = message;
		this.httpStatus = httpStatus;
	}

	public int getCode() {
		return code;
	}
	public String getMessage() {
		return message;
	}
	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
	public boolean isError() {
		return httpStatus.value() >= 400;
	}
	
}
