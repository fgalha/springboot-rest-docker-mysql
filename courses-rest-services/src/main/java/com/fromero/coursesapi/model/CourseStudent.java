package com.fromero.coursesapi.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Entity
@Table(name = "course_student")
@Data
public class CourseStudent {

	@JsonIgnore
	@EmbeddedId
	private CourseStudentKey id;

	@JsonIgnore
    @ManyToOne
    @MapsId("studentId")
    @JoinColumn(name = "student_id")
    @JsonBackReference
    private Student student;

	@JsonIgnore
    @ManyToOne
    @MapsId("courseId")
    @JoinColumn(name = "course_id")
    @JsonBackReference
    private Course course;

	@JsonIgnore
    @Column(name = "rating")
    @Min(0)
    @Max(100)
    private Integer rating;	
	
}
