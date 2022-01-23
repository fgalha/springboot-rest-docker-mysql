package com.fromero.coursesapi.service;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import com.fromero.coursesapi.error.ApiReturnMessage;
import com.fromero.coursesapi.errorhandling.BusinessErrorException;
import com.fromero.coursesapi.model.Course;
import com.fromero.coursesapi.model.CourseStudent;
import com.fromero.coursesapi.model.CourseStudentKey;
import com.fromero.coursesapi.model.Student;
import com.fromero.coursesapi.repository.CourseRepository;
import com.fromero.coursesapi.repository.CourseStudentRepository;
import com.fromero.coursesapi.repository.StudentRepository;

@Service
@Transactional
public class StudentService implements Serializable {

	private static final long serialVersionUID = 1L;

	@Value("${courses.api.maximumCoursesByStudent:5}")
	private int maximumCoursesByStudent;

	@Value("${courses.api.maximumStudentsByCourse:50}")
	private int maximumStudentsByCourse;

	@Autowired
	private StudentRepository studentRepository;
	
	@Autowired
	private CourseStudentRepository courseStudentRepository;

	@Autowired
	private CourseRepository courseRepository;

	public List<Student> listAllStudents() {
		return Streamable.of(studentRepository.findAll()).toList();
	}

	public void saveStudent(Student student) {
		studentRepository.save(student);
	}

	public Student getStudent(Integer id) {
		return studentRepository.findById(id).get();
	}

	public void deleteStudent(Integer id) {
		studentRepository.deleteById(id);
	}

	public void registerCourse(Student student, Course course) {
		CourseStudentKey key = new CourseStudentKey(student.getId(), course.getId());
		Optional<CourseStudent> op = courseStudentRepository.findById(key);
		if (op.isPresent()) {
			throw new BusinessErrorException(ApiReturnMessage.ERR_STUDENT_ALREADY_ENROLLED, student.getId(), course.getId());
		}
		int totalCourses = studentRepository.getTotalCoursesEnrolled(student.getId());
		if (totalCourses > maximumCoursesByStudent) {
			throw new BusinessErrorException(ApiReturnMessage.ERR_MAXIMUM_COURSES_BY_STUDENT_REACHED, student.getId(), maximumCoursesByStudent);
		}
		int totalStudents = courseRepository.getTotalStudentsEnrolled(course.getId());
		if (totalStudents > maximumStudentsByCourse) {
			throw new BusinessErrorException(ApiReturnMessage.ERR_MAXIMUM_STUDENTS_BY_COURSE_REACHED, course.getId(), maximumStudentsByCourse);
		}
		CourseStudent cs = new CourseStudent();
		cs.setId(key);
		cs.setStudent(student);
		cs.setCourse(course);
		courseStudentRepository.save(cs);
	}

	public void unregisterCourse(Student student, Course course) {
		CourseStudentKey key = new CourseStudentKey(student.getId(), course.getId());
		Optional<CourseStudent> op = courseStudentRepository.findById(key);
		if (!op.isPresent()) {
			throw new BusinessErrorException(ApiReturnMessage.ERR_STUDENT_IS_NOT_ENROLLED, student.getId(), course.getId());
		}
		int totalCourses = studentRepository.getTotalCoursesEnrolled(student.getId());
		if (totalCourses > maximumCoursesByStudent) {
			throw new BusinessErrorException(ApiReturnMessage.ERR_MAXIMUM_COURSES_BY_STUDENT_REACHED, student.getId(), maximumCoursesByStudent);
		}
		int totalStudents = courseRepository.getTotalStudentsEnrolled(course.getId());
		if (totalStudents > maximumStudentsByCourse) {
			throw new BusinessErrorException(ApiReturnMessage.ERR_MAXIMUM_STUDENTS_BY_COURSE_REACHED, course.getId(), maximumStudentsByCourse);
		}
		CourseStudent cs = new CourseStudent();
		cs.setId(key);
		cs.setStudent(student);
		cs.setCourse(course);
		courseStudentRepository.deleteById(key);
	}

	public List<Student> listAllStudentsByCourse(Integer idcourse) {
		return studentRepository.listByCourses(idcourse);
	}

	public List<Student> listAllStudentsWithoutCourses() {
		return studentRepository.listAllStudentsWithoutCourses();
	}
}
