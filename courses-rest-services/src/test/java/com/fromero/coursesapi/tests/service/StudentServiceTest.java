package com.fromero.coursesapi.tests.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.BeanUtils;

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
import com.fromero.coursesapi.service.ParameterService;
import com.fromero.coursesapi.service.StudentService;

public class StudentServiceTest {

	private static final int MAXIMUM_COURSES_BY_STUDENT = 5;
	private static final int MAXIMUM_STUDENTS_BY_COURSE = 50;
	private static final Integer RATING_MINIMUM = 0;
	private static final Integer RATING_MAXIMUM = 100;

	@Mock
	private ParameterService parameterService;

	@Mock
	private StudentRepository studentRepository;

	@Mock
	private CourseStudentRepository courseStudentRepository;

	@Mock
	private CourseRepository courseRepository;

	@InjectMocks
	private StudentService studentService;

	@BeforeEach
	private void beforeEach() {
		MockitoAnnotations.openMocks(this);
		int i = 0;
		List<Student> students = new ArrayList<Student>();
		students.add(createStudent(++i, "Alice Test", "alice.test@abc123.com"));
		students.add(createStudent(++i, "Bob Test", "bob.test@abc123.com"));
		students.add(createStudent(++i, "Clarice Test", "clarice.test@abc123.com"));
		students.add(createStudent(++i, "Dennis Test", "dennis.test@abc123.com"));
		students.add(createStudent(++i, "Eva Test", "eva.test@abc123.com"));
		when(studentRepository.findAll()).thenReturn(students);
		when(studentRepository.save(any(Student.class))).thenAnswer(new Answer<Student>() {
			public Student answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				Student st = (Student) args[0];
				st.setId(1);
				return st;
			}
		});
		when(studentRepository.findById(any(Integer.class))).thenReturn(Optional.of(students.get(0)));
		when(parameterService.getMaximumCoursesByStudent()).thenReturn(MAXIMUM_COURSES_BY_STUDENT);
		when(parameterService.getMaximumStudentsByCourse()).thenReturn(MAXIMUM_STUDENTS_BY_COURSE);
		when(parameterService.getMinimumRating()).thenReturn(RATING_MINIMUM);
		when(parameterService.getMaximumRating()).thenReturn(RATING_MAXIMUM);
	}

	private Student createStudent(Integer id, String name, String email) {
		return new Student(id, name, email, null);
	}

	@Test
	public void testListAllStudents() {
		List<Student> students = studentService.listAllStudents();
		assertEquals(5, students.size(), "Number of students does not match");
		assertEquals("Alice Test",
				students.stream().filter(s -> s.getId() == 1).collect(Collectors.toList()).get(0).getName(),
				"Number of students does not match");
	}

	@Test
	public void testSaveStudent() {
		Student student = createStudent(null, "Alice Test", "alice.test@abc123.com");
		Student saved = studentRepository.save(student);
		assertNotNull(saved.getId());
	}

	@Test
	public void testGetStudent_idNotInformed() {
		BusinessErrorException exception = assertThrows(BusinessErrorException.class, () -> {
			studentService.getStudent(null);
		});
		assertEquals(ApiReturnMessage.ERR_ID_NOT_INFORMED, exception.getApiReturnMessage());		
	}

	@Test
	public void testGetStudent_success() {
		Student student = studentService.getStudent(1);
		assertNotNull(student);
		assertEquals(1, student.getId());
	}

	@Test
	public void testDeleteStudent() {
		studentService.deleteStudent(1);
	}
	
	@Test
	public void testRegisterCourse_studentAlreadyEnrolled() {
		Student student = createStudent(1, "Alice", "alice@test.com");
		Course course = new Course(1, "Algorithms", null);
		CourseStudentKey key = new CourseStudentKey(1, 1);
		CourseStudent cs = new CourseStudent();
		cs.setId(key);
		cs.setStudent(createStudent(1, "Alice", "alice@test.com"));
		cs.setCourse(new Course(1, "Algorithms", null));
		
		when(courseStudentRepository.findById(key)).thenReturn(Optional.of(cs));
		
		BusinessErrorException exception = assertThrows(BusinessErrorException.class, () -> {
			studentService.registerCourse(student, course);
		});
		assertEquals(ApiReturnMessage.ERR_STUDENT_ALREADY_ENROLLED, exception.getApiReturnMessage());
	}

	@Test
	public void testRegisterCourse_studentMaximumCoursesReached() {
		Student student = createStudent(1, "Alice", "alice@test.com");
		Course course = new Course(1, "Algorithms", null);
		CourseStudentKey key = new CourseStudentKey(1, 1);
		CourseStudent cs = new CourseStudent();
		cs.setId(key);
		cs.setStudent(createStudent(1, "Alice", "alice@test.com"));
		cs.setCourse(new Course(1, "Algorithms", null));
		
		when(studentRepository.getTotalCoursesEnrolled(any(Integer.class))).thenReturn(MAXIMUM_COURSES_BY_STUDENT);
		
		BusinessErrorException exception = assertThrows(BusinessErrorException.class, () -> {
			studentService.registerCourse(student, course);
		});
		assertEquals(ApiReturnMessage.ERR_MAXIMUM_COURSES_BY_STUDENT_REACHED, exception.getApiReturnMessage());
	}
	
	@Test
	public void testRegisterCourse_studentMaximumStudentsReached() {
		Student student = createStudent(1, "Alice", "alice@test.com");
		Course course = new Course(1, "Algorithms", null);
		CourseStudentKey key = new CourseStudentKey(1, 1);
		CourseStudent cs = new CourseStudent();
		cs.setId(key);
		cs.setStudent(createStudent(1, "Alice", "alice@test.com"));
		cs.setCourse(new Course(1, "Algorithms", null));
		
		when(courseRepository.getTotalStudentsEnrolled(any(Integer.class))).thenReturn(MAXIMUM_STUDENTS_BY_COURSE);
		
		BusinessErrorException exception = assertThrows(BusinessErrorException.class, () -> {
			studentService.registerCourse(student, course);
		});
		assertEquals(ApiReturnMessage.ERR_MAXIMUM_STUDENTS_BY_COURSE_REACHED, exception.getApiReturnMessage());
	}

	@Test
	public void testRegisterCourse_success() {
		Student student = createStudent(1, "Alice", "alice@test.com");
		Course course = new Course(1, "Algorithms", null);
		CourseStudentKey key = new CourseStudentKey(1, 1);
		CourseStudent cs = new CourseStudent();
		cs.setId(key);
		cs.setStudent(createStudent(1, "Alice", "alice@test.com"));
		cs.setCourse(new Course(1, "Algorithms", null));
		
		when(studentRepository.getTotalCoursesEnrolled(any(Integer.class))).thenReturn(MAXIMUM_COURSES_BY_STUDENT - 1);
		when(courseRepository.getTotalStudentsEnrolled(any(Integer.class))).thenReturn(MAXIMUM_STUDENTS_BY_COURSE - 1);
		CourseStudent saved = new CourseStudent();
		BeanUtils.copyProperties(cs, new CourseStudent());
		when(courseStudentRepository.save(any(CourseStudent.class))).thenReturn(saved);
		studentService.registerCourse(student, course);
	}

	@Test
	public void testUnregisterCourse_studentNotEnrolled() {
		Student student = createStudent(1, "Alice", "alice@test.com");
		Course course = new Course(1, "Algorithms", null);
		CourseStudentKey key = new CourseStudentKey(1, 1);
		CourseStudent cs = new CourseStudent();
		cs.setId(key);
		cs.setStudent(createStudent(1, "Alice", "alice@test.com"));
		cs.setCourse(new Course(1, "Algorithms", null));

		when(courseStudentRepository.findById(any(CourseStudentKey.class))).thenReturn(Optional.empty());
		
		BusinessErrorException exception = assertThrows(BusinessErrorException.class, () -> {
			studentService.unregisterCourse(student, course);
		});
		assertEquals(ApiReturnMessage.ERR_STUDENT_IS_NOT_ENROLLED, exception.getApiReturnMessage());
	}

	@Test
	public void testUnregisterCourse_success() {
		Student student = createStudent(1, "Alice", "alice@test.com");
		Course course = new Course(1, "Algorithms", null);
		CourseStudentKey key = new CourseStudentKey(1, 1);
		CourseStudent cs = new CourseStudent();
		cs.setId(key);
		cs.setStudent(createStudent(1, "Alice", "alice@test.com"));
		cs.setCourse(new Course(1, "Algorithms", null));

		when(courseStudentRepository.findById(any(CourseStudentKey.class))).thenReturn(Optional.of(cs));
		studentService.unregisterCourse(student, course);
	}

	@Test
	public void testListAllStudentsByCourse_idNotInformed() {
		BusinessErrorException exception = assertThrows(BusinessErrorException.class, () -> {
			studentService.listAllStudentsByCourse(null);
		});
		assertEquals(ApiReturnMessage.ERR_ID_NOT_INFORMED, exception.getApiReturnMessage());
	}

	@Test
	public void testListAllStudentsByCourse_success() {
		int i = 0;
		StudentDTO s1 = new StudentDTO(createStudent(++i, "Alice Test", "alice.test@abc123.com"), 90);
		StudentDTO s2 = new StudentDTO(createStudent(++i, "Bob Test", "bob.test@abc123.com"), 95);
		when(studentRepository.listByCourses(any(Integer.class))).thenReturn(List.of(s1, s2));
		
		List<StudentDTO> list = studentService.listAllStudentsByCourse(1);
		assertEquals(2, list.size());
		assertEquals(list.get(0).getRating(), 90);
		assertEquals(list.get(1).getRating(), 95);
	}

	@Test
	public void testListAllStudentsWithoutCourses() {
		int i = 0;
		Student s1 = createStudent(++i, "Alice Test", "alice.test@abc123.com");
		Student s2 = createStudent(++i, "Bob Test", "bob.test@abc123.com");
		
		when(studentRepository.listAllStudentsWithoutCourses()).thenReturn(List.of(s1, s2));
		
		List<Student> list = studentService.listAllStudentsWithoutCourses();
		assertEquals(2, list.size());
		assertEquals(list.get(0).getId(), 1);
		assertEquals(list.get(1).getId(), 2);
	}

	@Test
	public void testUpdateStudentRating_outOfRangeMaximum() {
		Student student = createStudent(1, "Alice", "alice@test.com");
		Course course = new Course(1, "Algorithms", null);
		CourseStudentKey key = new CourseStudentKey(1, 1);
		CourseStudent cs = new CourseStudent();
		cs.setId(key);
		cs.setStudent(createStudent(1, "Alice", "alice@test.com"));
		cs.setCourse(new Course(1, "Algorithms", null));
		
		Rating rating = new Rating(101);

		BusinessErrorException exception = assertThrows(BusinessErrorException.class, () -> {
			studentService.updateStudentRating(student, course, rating);
		});
		assertEquals(ApiReturnMessage.ERR_RATING_OUT_OF_RANGE, exception.getApiReturnMessage());
	}

	@Test
	public void testUpdateStudentRating_outOfRangeMinimum() {
		Student student = createStudent(1, "Alice", "alice@test.com");
		Course course = new Course(1, "Algorithms", null);
		CourseStudentKey key = new CourseStudentKey(1, 1);
		CourseStudent cs = new CourseStudent();
		cs.setId(key);
		cs.setStudent(createStudent(1, "Alice", "alice@test.com"));
		cs.setCourse(new Course(1, "Algorithms", null));
		
		Rating rating = new Rating(-1);

		BusinessErrorException exception = assertThrows(BusinessErrorException.class, () -> {
			studentService.updateStudentRating(student, course, rating);
		});
		assertEquals(ApiReturnMessage.ERR_RATING_OUT_OF_RANGE, exception.getApiReturnMessage());
	}

	@Test
	public void testUpdateStudentRating_studentNotEnrolled() {
		Student student = createStudent(1, "Alice", "alice@test.com");
		Course course = new Course(1, "Algorithms", null);
		CourseStudentKey key = new CourseStudentKey(1, 1);
		CourseStudent cs = new CourseStudent();
		cs.setId(key);
		cs.setStudent(createStudent(1, "Alice", "alice@test.com"));
		cs.setCourse(new Course(1, "Algorithms", null));

		when(courseStudentRepository.findById(key)).thenReturn(Optional.empty());
		
		Rating rating = new Rating(100);
		BusinessErrorException exception = assertThrows(BusinessErrorException.class, () -> {
			studentService.updateStudentRating(student, course, rating);
		});
		assertEquals(ApiReturnMessage.ERR_STUDENT_IS_NOT_ENROLLED, exception.getApiReturnMessage());
	}

	@Test
	public void testUpdateStudentRating_success() {
		Student student = createStudent(1, "Alice", "alice@test.com");
		Course course = new Course(1, "Algorithms", null);
		CourseStudentKey key = new CourseStudentKey(1, 1);
		CourseStudent cs = new CourseStudent();
		cs.setId(key);
		cs.setStudent(createStudent(1, "Alice", "alice@test.com"));
		cs.setCourse(new Course(1, "Algorithms", null));
		
		Rating rating = new Rating(100);
		
		CourseStudent saved = new CourseStudent();
		BeanUtils.copyProperties(cs, saved);
		
		when(courseStudentRepository.findById(key)).thenReturn(Optional.of(saved));
		when(courseStudentRepository.save(any(CourseStudent.class))).thenReturn(saved);

		studentService.updateStudentRating(student, course, rating);
	}
}
