package com.fromero.coursesapi.dto;

import java.io.Serializable;

import com.fromero.coursesapi.model.Student;

import lombok.Data;

@Data
public class StudentDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id;
    private String name;
    private String email;
	private Integer rating;

	public StudentDTO(Student student) {
		this.id = student.getId();
		this.name = student.getName();
		this.email = student.getEmail();
	}

	public StudentDTO(Student student, int rating) {
		this(student);
		this.rating = rating;
	}

}
