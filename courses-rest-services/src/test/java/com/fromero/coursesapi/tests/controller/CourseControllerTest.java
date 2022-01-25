package com.fromero.coursesapi.tests.controller;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fromero.coursesapi.controller.v1.CourseController;
import com.fromero.coursesapi.dto.StudentDTO;
import com.fromero.coursesapi.model.Course;
import com.fromero.coursesapi.model.Student;
import com.fromero.coursesapi.service.CourseService;
import com.fromero.coursesapi.service.StudentService;

@WebMvcTest(CourseController.class)
public class CourseControllerTest {

	private static final String SERVICE_VERSION = "/v1/courses";

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private CourseService courseService;

	@MockBean
	private StudentService studentService;

	private List<Course> courses;

	@BeforeEach
	private void beforeTestClass() {
		int i = 0;
		courses = new ArrayList<Course>();
		courses.add(createCourse(++i, "Algorithms I"));
		courses.add(createCourse(++i, "Algorithms II"));
		courses.add(createCourse(++i, "Operational Systems"));
		courses.add(createCourse(++i, "Machine Learning I"));
		courses.add(createCourse(++i, "Machine Learning II"));
		when(courseService.listAllCourses()).thenReturn(courses);
		when(courseService.getCourse(1)).thenReturn(courses.get(0));
		when(courseService.getCourse(6)).thenThrow(new NoSuchElementException());

		Course courseNew = createCourse(null, "Software Engineering");
		when(courseService.saveCourse(courseNew)).thenAnswer(new Answer<Course>() {
			public Course answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				Course st = (Course) args[0];
				st.setId(99);
				return st;
			}
		});

		Course courseUpdate = createCourse(50, "Software Engineering II");
		when(courseService.saveCourse(courseUpdate)).thenAnswer(new Answer<Course>() {
			public Course answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				Course st = (Course) args[0];
				st.setId(50);
				return st;
			}
		});

		List<StudentDTO> students = new ArrayList<StudentDTO>();
		students.add(createStudentDTO(++i, "Alice Test", "alice.test@abc123.com", 99));
		students.add(createStudentDTO(++i, "Bob Test", "bob.test@abc123.com", 55));
		students.add(createStudentDTO(++i, "Clarice Test", "clarice.test@abc123.com", 67));
		students.add(createStudentDTO(++i, "Dennis Test", "dennis.test@abc123.com", 86));
		students.add(createStudentDTO(++i, "Eva Test", "eva.test@abc123.com", 40));

		when(studentService.listAllStudentsByCourse(any(Integer.class))).thenReturn(students);
		when(courseService.listAllCoursesWithoutStudents()).thenReturn(courses.subList(3, 5));
	}

	private StudentDTO createStudentDTO(Integer id, String name, String email, Integer rating) {
		return new StudentDTO(new Student(id, name, email, null), rating);
	}

	private Course createCourse(Integer id, String name) {
		return new Course(id, name, null);
	}

	@Test
	public void test_list() throws Exception {
		this.mockMvc.perform(get(SERVICE_VERSION + "/list")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().string(containsString(
						"{\"id\":1,\"name\":\"Algorithms I\"},{\"id\":2,\"name\":\"Algorithms II\"},"
						+ "{\"id\":3,\"name\":\"Operational Systems\"},"
						+ "{\"id\":4,\"name\":\"Machine Learning I\"},"
						+ "{\"id\":5,\"name\":\"Machine Learning II\"}")));
	}

	@Test
	public void test_get_success() throws Exception {
		when(courseService.listAllCourses()).thenReturn(courses);
		this.mockMvc.perform(get(SERVICE_VERSION + "/1")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().string(containsString("{\"id\":1,\"name\":\"Algorithms I\"}")));
	}

	@Test
	public void test_get_not_exists() throws Exception {
		this.mockMvc.perform(get(SERVICE_VERSION + "/6")).andDo(print()).andExpect(status().isNotFound())
				.andExpect(content().string(containsString(
						"{\"code\":10001,\"message\":\"Course id = 6 not found\",\"httpStatus\":\"NOT_FOUND\"}")));
	}

	@Test
	public void test_post_success() throws Exception {
		this.mockMvc
				.perform(post(SERVICE_VERSION + "/").content(asJsonString(new Course(null, "Algorithms I", null)))
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isOk()).andExpect(content().string(containsString(
						"{\"code\":10,\"message\":\"Course registered. Id = 99\",\"httpStatus\":\"OK\"}")));
	}

	@Test
	public void test_put_success() throws Exception {
		this.mockMvc
				.perform(put(SERVICE_VERSION + "/50")
						.content(asJsonString(new Course(50, "Software Engineering II", null)))
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
				.andDo(print()).andExpect(status().isOk()).andExpect(content().string(containsString(
						"{\"code\":11,\"message\":\"Course data updated. Id = 50\",\"httpStatus\":\"OK\"}")));
	}

	@Test
	public void test_delete_success() throws Exception {
		this.mockMvc
				.perform(delete(SERVICE_VERSION + "/50"))
				.andDo(print()).andExpect(status().isOk()).andExpect(content().string(containsString(
						"{\"code\":12,\"message\":\"Course excluded. Id = 50\",\"httpStatus\":\"OK\"}")));
	}

	@Test
	public void test_list_students_by_course() throws Exception {
		this.mockMvc.perform(get(SERVICE_VERSION + "/1/list-students")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().string(containsString(
						"{\"id\":6,\"name\":\"Alice Test\",\"email\":\"alice.test@abc123.com\",\"rating\":99},"
						+ "{\"id\":7,\"name\":\"Bob Test\",\"email\":\"bob.test@abc123.com\",\"rating\":55},"
						+ "{\"id\":8,\"name\":\"Clarice Test\",\"email\":\"clarice.test@abc123.com\",\"rating\":67},"
						+ "{\"id\":9,\"name\":\"Dennis Test\",\"email\":\"dennis.test@abc123.com\",\"rating\":86},"
						+ "{\"id\":10,\"name\":\"Eva Test\",\"email\":\"eva.test@abc123.com\",\"rating\":40}")));
	}

	@Test
	public void test_list_courses_without_students() throws Exception {
		this.mockMvc.perform(get(SERVICE_VERSION + "/list-no-students")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().string(containsString(
						"[{\"id\":4,\"name\":\"Machine Learning I\"},{\"id\":5,\"name\":\"Machine Learning II\"}]")));
	}

	public static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
