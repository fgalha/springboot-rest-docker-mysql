package com.fromero.coursesapi.dto;

import java.io.Serializable;

import com.fromero.coursesapi.model.Course;

import lombok.Data;

@Data
public class CourseDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
    private String name;
	private Integer rating;

	public CourseDTO(Course course) {
		this.id = course.getId();
		this.name = course.getName(); 
	}

	public CourseDTO(Course course, int rating) {
		this(course);
		this.rating = rating;
	}

}
