package com.fromero.coursesapi.service;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import com.fromero.coursesapi.dto.Rating;
import com.fromero.coursesapi.dto.StudentDTO;
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

	@Autowired
	private ParameterService parameterService;

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
			throw new BusinessErrorException(ApiReturnMessage.ERR_STUDENT_ALREADY_ENROLLED, student.getId(),
					course.getId());
		}
		int totalCourses = studentRepository.getTotalCoursesEnrolled(student.getId());
		if (totalCourses > parameterService.getMaximumCoursesByStudent()) {
			throw new BusinessErrorException(ApiReturnMessage.ERR_MAXIMUM_COURSES_BY_STUDENT_REACHED, student.getId(),
					parameterService.getMaximumCoursesByStudent());
		}
		int totalStudents = courseRepository.getTotalStudentsEnrolled(course.getId());
		if (totalStudents > parameterService.getMaximumStudentsByCourse()) {
			throw new BusinessErrorException(ApiReturnMessage.ERR_MAXIMUM_STUDENTS_BY_COURSE_REACHED, course.getId(),
					parameterService.getMaximumStudentsByCourse());
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
			throw new BusinessErrorException(ApiReturnMessage.ERR_STUDENT_IS_NOT_ENROLLED, student.getId(),
					course.getId());
		}
		CourseStudent cs = new CourseStudent();
		cs.setId(key);
		cs.setStudent(student);
		cs.setCourse(course);
		courseStudentRepository.deleteById(key);
	}

	public List<StudentDTO> listAllStudentsByCourse(Integer idcourse) {
		return studentRepository.listByCourses(idcourse);
	}

	public List<Student> listAllStudentsWithoutCourses() {
		return studentRepository.listAllStudentsWithoutCourses();
	}

	public void updateStudentRating(Student student, Course course, Rating rating) {
		if (rating.getValue() < parameterService.getMinimumRating()
				|| rating.getValue() > parameterService.getMaximumRating()) {
			throw new BusinessErrorException(ApiReturnMessage.ERR_RATING_OUT_OF_RANGE, rating.getValue(), 
					parameterService.getMinimumRating(), parameterService.getMaximumRating());
		}
		CourseStudentKey key = new CourseStudentKey(student.getId(), course.getId());
		Optional<CourseStudent> op = courseStudentRepository.findById(key);
		if (!op.isPresent()) {
			throw new BusinessErrorException(ApiReturnMessage.ERR_STUDENT_IS_NOT_ENROLLED, student.getId(),
					course.getId());
		}
		CourseStudent cs = op.get();
		cs.setRating(rating.getValue());
		courseStudentRepository.save(cs);
	}
}
