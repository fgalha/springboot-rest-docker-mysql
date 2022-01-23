package com.fromero.coursesapi.service;

import java.io.Serializable;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Provide business parameters.
 * Now it is using file parameters, but can easily change to a repository.
 * @author fromero.
 */
@Service
public class ParameterService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Value("${courses.api.maximumCoursesByStudent:5}")
	private int maximumCoursesByStudent;

	@Value("${courses.api.maximumStudentsByCourse:50}")
	private int maximumStudentsByCourse;

	@Value("${courses.api.minimumRating:0}")
	private int minimumRating;

	@Value("${courses.api.maximumRating:100}")
	private int maximumRating;

	public int getMaximumCoursesByStudent() {
		return maximumCoursesByStudent;
	}

	public int getMaximumStudentsByCourse() {
		return maximumStudentsByCourse;
	}

	public int getMinimumRating() {
		return minimumRating;
	}

	public int getMaximumRating() {
		return maximumRating;
	}

	
	
}
