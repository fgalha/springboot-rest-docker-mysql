package com.fromero.coursesapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.fromero.coursesapi.model.Student;

public interface StudentRepository extends CrudRepository<Student, Integer> {

	@Query("select count(c) from CourseStudent c where c.id.studentId = :studentId")
	int getTotalCoursesEnrolled(Integer studentId);

	@Query("select distinct s from Student s inner join s.courses cs where cs.id.courseId = :idcourse")
	List<Student> listByCourses(Integer idcourse);

	@Query("select distinct s from Student s where not exists (select cs from CourseStudent cs where cs.id.studentId = s.id)")
	List<Student> listAllStudentsWithoutCourses();
}
