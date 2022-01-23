package com.fromero.coursesapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.fromero.coursesapi.model.Course;

public interface CourseRepository extends CrudRepository<Course, Integer> {

	@Query("select count(c) from CourseStudent c where c.id.courseId = :courseId")
	int getTotalStudentsEnrolled(Integer courseId);

	@Query("select distinct c from Course c inner join c.students cs where cs.id.studentId = :idstudent")
	List<Course> listCoursesByStudent(Integer idstudent);

	@Query("select distinct c from Course c where not exists (select cs from CourseStudent cs where cs.id.courseId = c.id)")
	List<Course> listAllCoursesWithoutStudents();
}
