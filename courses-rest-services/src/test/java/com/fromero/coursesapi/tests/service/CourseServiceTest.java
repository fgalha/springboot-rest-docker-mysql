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

import com.fromero.coursesapi.dto.CourseDTO;
import com.fromero.coursesapi.error.ApiReturnMessage;
import com.fromero.coursesapi.errorhandling.BusinessErrorException;
import com.fromero.coursesapi.model.Course;
import com.fromero.coursesapi.repository.CourseRepository;
import com.fromero.coursesapi.service.CourseService;

public class CourseServiceTest {

	@Mock
	private CourseRepository courseRepository;

	@InjectMocks
	private CourseService courseService;

	@BeforeEach
	private void beforeEach() {
		MockitoAnnotations.openMocks(this);
		int i = 0;
		List<Course> courses = new ArrayList<Course>();
		courses.add(createCourse(++i, "Algorithms I"));
		courses.add(createCourse(++i, "Algorithms II"));
		courses.add(createCourse(++i, "Operational Systems"));
		courses.add(createCourse(++i, "Machine Learning I"));
		courses.add(createCourse(++i, "Machine Learning II"));
		when(courseRepository.findAll()).thenReturn(courses);
		when(courseRepository.save(any(Course.class))).thenAnswer(new Answer<Course>() {
			public Course answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				Course st = (Course) args[0];
				st.setId(1);
				return st;
			}
		});
		when(courseRepository.findById(any(Integer.class))).thenReturn(Optional.of(courses.get(0)));
	}

	private Course createCourse(Integer id, String name) {
		return new Course(id, name, null);
	}

	@Test
    public void testListAllCourses() {
    	List<Course> courses = courseService.listAllCourses();
		assertEquals(5, courses.size(), "Number of students does not match");
		assertEquals("Algorithms I",
				courses.stream().filter(s -> s.getId() == 1).collect(Collectors.toList()).get(0).getName(),
				"Number of students does not match");
    }

	@Test
    public void testSaveCourse() {
		Course course = createCourse(null, "Database I");
		Course saved = courseRepository.save(course);
		assertNotNull(saved.getId());

    }

	@Test
	public void testGetCourse_idNotInformed() {
		BusinessErrorException exception = assertThrows(BusinessErrorException.class, () -> {
			courseService.getCourse(null);
		});
		assertEquals(ApiReturnMessage.ERR_ID_NOT_INFORMED, exception.getApiReturnMessage());		
	}


	@Test
    public void testGetCourse_success() {
		Course student = courseService.getCourse(1);
		assertNotNull(student);
		assertEquals(1, student.getId());
    }

	@Test
    public void testDeleteCourse_idNotInformed() {
		BusinessErrorException exception = assertThrows(BusinessErrorException.class, () -> {
			courseService.deleteCourse(null);
		});
		assertEquals(ApiReturnMessage.ERR_ID_NOT_INFORMED, exception.getApiReturnMessage());				
    }

	@Test
    public void testDeleteCourse_success() {
    	courseService.deleteCourse(1);
    }

	@Test
	public void testListAllCoursesByStudent_idNotInformed() {
		BusinessErrorException exception = assertThrows(BusinessErrorException.class, () -> {
			courseService.listAllCoursesByStudent(null);
		});
		assertEquals(ApiReturnMessage.ERR_ID_NOT_INFORMED, exception.getApiReturnMessage());			
	}

	@Test
	public void testListAllCoursesByStudent_success() {
		int i = 0;
		List<CourseDTO> courses = new ArrayList<CourseDTO>();
		courses.add(new CourseDTO(createCourse(++i, "Algorithms I"), 80));
		courses.add(new CourseDTO(createCourse(++i, "Algorithms II"), 90));

		when(courseRepository.listCoursesByStudent(any(Integer.class))).thenReturn(courses);
		
		List<CourseDTO> list = courseService.listAllCoursesByStudent(1);
		assertEquals(2, list.size());
		assertEquals(list.get(0).getId(), 1);
		assertEquals(list.get(1).getId(), 2);
	}

	@Test
	public void listAllCoursesWithoutStudents() {
		int i = 0;
		List<Course> courses = new ArrayList<Course>();
		courses.add(createCourse(++i, "Algorithms I"));
		courses.add(createCourse(++i, "Algorithms II"));

		when(courseRepository.listAllCoursesWithoutStudents()).thenReturn(courses);
		
		List<Course> list = courseService.listAllCoursesWithoutStudents();
		
		assertEquals(2, list.size());
		assertEquals(list.get(0).getId(), 1);
		assertEquals(list.get(1).getId(), 2);
	}
}
