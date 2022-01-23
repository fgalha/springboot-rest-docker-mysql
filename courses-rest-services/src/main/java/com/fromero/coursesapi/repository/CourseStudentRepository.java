package com.fromero.coursesapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fromero.coursesapi.model.CourseStudent;
import com.fromero.coursesapi.model.CourseStudentKey;

public interface CourseStudentRepository extends JpaRepository<CourseStudent, CourseStudentKey> {

}
