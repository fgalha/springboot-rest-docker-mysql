package com.fromero.coursesapi.tests.controller;

import static org.hamcrest.Matchers.containsString;
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
import java.util.stream.Collectors;

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
import com.fromero.coursesapi.controller.v1.StudentController;
import com.fromero.coursesapi.dto.CourseDTO;
import com.fromero.coursesapi.dto.Rating;
import com.fromero.coursesapi.error.ApiReturnMessage;
import com.fromero.coursesapi.errorhandling.BusinessErrorException;
import com.fromero.coursesapi.model.Course;
import com.fromero.coursesapi.model.Student;
import com.fromero.coursesapi.service.CourseService;
import com.fromero.coursesapi.service.StudentService;

@WebMvcTest(StudentController.class)
public class StudentControllerTest {

	private static final String SERVICE_VERSION = "/v1/students";

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
		List<Student> students = new ArrayList<Student>();
		students.add(createStudent(++i, "Alice Test", "alice.test@abc123.com"));
		students.add(createStudent(++i, "Bob Test", "bob.test@abc123.com"));
		students.add(createStudent(++i, "Clarice Test", "clarice.test@abc123.com"));
		students.add(createStudent(++i, "Dennis Test", "dennis.test@abc123.com"));
		students.add(createStudent(++i, "Eva Test", "eva.test@abc123.com"));

		int j = 0;
		courses = new ArrayList<Course>();
		courses.add(createCourse(++j, "Algorithms I"));
		courses.add(createCourse(++j, "Algorithms II"));
		courses.add(createCourse(++j, "Operational Systems"));
		courses.add(createCourse(++j, "Machine Learning I"));
		courses.add(createCourse(++j, "Machine Learning II"));

		when(studentService.listAllStudents()).thenReturn(students);
		when(studentService.getStudent(1)).thenReturn(students.get(0));
		when(studentService.getStudent(33)).thenThrow(new NoSuchElementException());
		
		Student studentNew = createStudent(null, "Uncle Bob", "uncle.bob@testing.com");
		when(studentService.saveStudent(studentNew)).thenAnswer(new Answer<Student>() {
			public Student answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				Student st = (Student) args[0];
				st.setId(99);
				return st;
			}
		});

		Student studentUpdated = createStudent(50, "Martin Fowler", "martin.fowler@testing.com");
		when(studentService.getStudent(50)).thenReturn(studentUpdated); 
		when(studentService.saveStudent(studentUpdated)).thenAnswer(new Answer<Student>() {
			public Student answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				Student st = (Student) args[0];
				st.setId(50);
				return st;
			}
		});
		
		when(studentService.getStudent(144)).thenThrow(new NoSuchElementException());
		
		// register course mocks - success
		Student student1 = students.get(0);
		Course course1 = courses.get(0);
		when(studentService.getStudent(1)).thenReturn(student1);
		when(courseService.getCourse(1)).thenReturn(course1);
		when(studentService.registerCourse(student1, course1)).thenReturn(true);
		
		// register course mocks - not found
		when(studentService.getStudent(300)).thenThrow(new NoSuchElementException());
		when(courseService.getCourse(300)).thenThrow(new NoSuchElementException());

		// register course mocks - error already enrolled
		Student student3 = students.get(2);
		Course course3 = courses.get(2);
		when(studentService.getStudent(3)).thenReturn(student3);
		when(courseService.getCourse(3)).thenReturn(course3);
		when(studentService.registerCourse(student3, course3)).thenThrow(new BusinessErrorException(
				ApiReturnMessage.ERR_STUDENT_ALREADY_ENROLLED, student3.getId(), course3.getId()));
		
		// register course mocks - error maximum courses reached
		Student student2 = students.get(1);
		Course course2 = courses.get(1);
		when(studentService.getStudent(2)).thenReturn(student2);
		when(courseService.getCourse(2)).thenReturn(course2);
		when(studentService.registerCourse(student2, course2)).thenThrow(new BusinessErrorException(
				ApiReturnMessage.ERR_MAXIMUM_COURSES_BY_STUDENT_REACHED, student2.getId(), 5));

		// register course mocks - error maximum courses reached
		Student student4 = students.get(3);
		Course course4 = courses.get(3);
		when(studentService.getStudent(4)).thenReturn(student4);
		when(courseService.getCourse(4)).thenReturn(course4);
		when(studentService.registerCourse(student4, course4)).thenThrow(new BusinessErrorException(
				ApiReturnMessage.ERR_MAXIMUM_STUDENTS_BY_COURSE_REACHED, student4.getId(), 50));

		// unregister course mocks - success
		Student student5 = students.get(4);
		Course course5 = courses.get(4);
		when(studentService.getStudent(5)).thenReturn(student5);
		when(courseService.getCourse(5)).thenReturn(course5);
		when(studentService.unregisterCourse(student5, course5)).thenReturn(true);
		
		// unregister course mocks - not found
		when(studentService.getStudent(400)).thenThrow(new NoSuchElementException());
		when(courseService.getCourse(400)).thenThrow(new NoSuchElementException());
		
		// list courses by user
		when(courseService.listAllCoursesByStudent(1))
				.thenReturn(courses.stream().map(c -> new CourseDTO(c, 90)).collect(Collectors.toList()));
		
		// list students without courses
		when(studentService.listAllStudentsWithoutCourses()).thenReturn(students.subList(1, 3));
		
		// update rating
		when(studentService.updateStudentRating(student1, course1, new Rating(95))).thenReturn(true);
	}

	private Student createStudent(Integer id, String name, String email) {
		return new Student(id, name, email, null);
	}

	private Course createCourse(Integer id, String name) {
		return new Course(id, name, null);
	}

	@Test
	public void test_list() throws Exception {
		this.mockMvc.perform(get(SERVICE_VERSION + "/list")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().string(containsString(
						"[{\"id\":1,\"name\":\"Alice Test\",\"email\":\"alice.test@abc123.com\"},"
						+ "{\"id\":2,\"name\":\"Bob Test\",\"email\":\"bob.test@abc123.com\"},"
						+ "{\"id\":3,\"name\":\"Clarice Test\",\"email\":\"clarice.test@abc123.com\"},"
						+ "{\"id\":4,\"name\":\"Dennis Test\",\"email\":\"dennis.test@abc123.com\"},"
						+ "{\"id\":5,\"name\":\"Eva Test\",\"email\":\"eva.test@abc123.com\"}]")));
	}

	@Test
	public void test_getbyid_success() throws Exception {
		this.mockMvc.perform(get(SERVICE_VERSION + "/1")).andDo(print()).andExpect(status().isOk())
				.andExpect(content().string(containsString(
						"{\"id\":1,\"name\":\"Alice Test\",\"email\":\"alice.test@abc123.com\"}")));
	}

	@Test
	public void test_getbyid_notfound() throws Exception {
		this.mockMvc.perform(get(SERVICE_VERSION + "/33")).andDo(print()).andExpect(status().isNotFound())
				.andExpect(content().string(containsString(
						"{\"code\":10000,\"message\":\"Student id = 33 not found\",\"httpStatus\":\"NOT_FOUND\"}")));
	}

    @Test
    public void test_post_success() throws Exception {
		this.mockMvc
		.perform(post(SERVICE_VERSION + "/").content(asJsonString(new Student(null, "Uncle Bob", "uncle.bob@testing.com", null)))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
		.andDo(print()).andExpect(status().isOk()).andExpect(content().string(containsString(
				"{\"code\":1,\"message\":\"Student registered. Id = 99\",\"httpStatus\":\"OK\"}")));
    }

    @Test
    public void test_put_success() throws Exception {
		this.mockMvc
		.perform(put(SERVICE_VERSION + "/50").content(asJsonString(new Student(null, "Uncle Bob", "uncle.bob@testing.com", null)))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
		.andDo(print()).andExpect(status().isOk()).andExpect(content().string(containsString(
				"{\"code\":2,\"message\":\"Student data updated. Id = 50\",\"httpStatus\":\"OK\"}")));
    }
    
    @Test
    public void test_put_notfound() throws Exception {
		this.mockMvc
		.perform(put(SERVICE_VERSION + "/144").content(asJsonString(new Student(null, "Uncle Bob", "uncle.bob@testing.com", null)))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
		.andDo(print()).andExpect(status().isNotFound()).andExpect(content().string(containsString(
				"{\"code\":10000,\"message\":\"Student id = 144 not found\",\"httpStatus\":\"NOT_FOUND\"}")));
    }
    
	@Test
	public void test_delete_success() throws Exception {
		this.mockMvc
				.perform(delete(SERVICE_VERSION + "/50"))
				.andDo(print()).andExpect(status().isOk()).andExpect(content().string(containsString(
						"{\"code\":3,\"message\":\"Student excluded. Id = 50\",\"httpStatus\":\"OK\"}")));
	}

	@Test
	public void test_delete_notfound() throws Exception {
		this.mockMvc
				.perform(delete(SERVICE_VERSION + "/144"))
				.andDo(print()).andExpect(status().isNotFound()).andExpect(content().string(containsString(
						"{\"code\":10000,\"message\":\"Student id = 144 not found\",\"httpStatus\":\"NOT_FOUND\"}")));
	}

	@Test
    public void test_registercourse_success() throws Exception {
		this.mockMvc
		.perform(post(SERVICE_VERSION + "/1/register/1"))
		.andDo(print()).andExpect(status().isOk()).andExpect(content().string(containsString(
				"{\"code\":21,\"message\":\"Student id = 1 is enrolled to the course id = 1 \",\"httpStatus\":\"OK\"}")));
    }

	@Test
    public void test_registercourse_studentnotfound() throws Exception {
		this.mockMvc
		.perform(post(SERVICE_VERSION + "/300/register/1"))
		.andDo(print()).andExpect(status().isNotFound()).andExpect(content().string(containsString(
				"{\"code\":10000,\"message\":\"Student id = 300 not found\",\"httpStatus\":\"NOT_FOUND\"}")));
    }

	@Test
    public void test_registercourse_coursenotfound() throws Exception {
		this.mockMvc
		.perform(post(SERVICE_VERSION + "/1/register/300"))
		.andDo(print()).andExpect(status().isNotFound()).andExpect(content().string(containsString(
				"{\"code\":10001,\"message\":\"Course id = 300 not found\",\"httpStatus\":\"NOT_FOUND\"}")));
    }

	@Test
    public void test_registercourse_alreadyenrolled() throws Exception {
		this.mockMvc
		.perform(post(SERVICE_VERSION + "/3/register/3"))
		.andDo(print()).andExpect(status().isBadRequest()).andExpect(content().string(containsString(
				"{\"code\":10004,\"message\":\"Student id = 3 is already enrolled to the course id = 3\",\"httpStatus\":\"BAD_REQUEST\"}")));
    }

	@Test
    public void test_registercourse_maximum_courses_reached() throws Exception {
		this.mockMvc
		.perform(post(SERVICE_VERSION + "/2/register/2"))
		.andDo(print()).andExpect(status().isBadRequest()).andExpect(content().string(containsString(
				"{\"code\":10002,\"message\":\"The student id = 2 reached the maximum of 5 courses.\",\"httpStatus\":\"BAD_REQUEST\"}")));
    }

	@Test
    public void test_registercourse_maximum_students_reached() throws Exception {
		this.mockMvc
		.perform(post(SERVICE_VERSION + "/4/register/4"))
		.andDo(print()).andExpect(status().isBadRequest()).andExpect(content().string(containsString(
				"{\"code\":10003,\"message\":\"The course id = 4 reached the maximum of 50 students.\",\"httpStatus\":\"BAD_REQUEST\"}")));
    }

	@Test
    public void test_unregistercourse_studentnotfound() throws Exception {
		this.mockMvc
		.perform(post(SERVICE_VERSION + "/400/unregister/5"))
		.andDo(print()).andExpect(status().isNotFound()).andExpect(content().string(containsString(
				"{\"code\":10000,\"message\":\"Student id = 400 not found\",\"httpStatus\":\"NOT_FOUND\"}")));
    }

	@Test
    public void test_unregistercourse_coursenotfound() throws Exception {
		this.mockMvc
		.perform(post(SERVICE_VERSION + "/5/unregister/400"))
		.andDo(print()).andExpect(status().isNotFound()).andExpect(content().string(containsString(
				"{\"code\":10001,\"message\":\"Course id = 400 not found\",\"httpStatus\":\"NOT_FOUND\"}")));
    }

	@Test
	public void test_list_courses_success() throws Exception {
		this.mockMvc.perform(get(SERVICE_VERSION + "/1/list-courses")).andDo(print()).andExpect(status().isOk())
		.andExpect(content().string(containsString(
				"[{\"id\":1,\"name\":\"Algorithms I\",\"rating\":90},{\"id\":2,\"name\":\"Algorithms II\",\"rating\":90},{\"id\":3,\"name\":\"Operational Systems\",\"rating\":90},{\"id\":4,\"name\":\"Machine Learning I\",\"rating\":90},{\"id\":5,\"name\":\"Machine Learning II\",\"rating\":90}]")));
    }

	@Test
	public void test_list_students_without_courses() throws Exception {
		this.mockMvc.perform(get(SERVICE_VERSION + "/list-no-courses")).andDo(print()).andExpect(status().isOk())
		.andExpect(content().string(containsString(
				"[{\"id\":2,\"name\":\"Bob Test\",\"email\":\"bob.test@abc123.com\"},{\"id\":3,\"name\":\"Clarice Test\",\"email\":\"clarice.test@abc123.com\"}]")));
    }
	
    @Test
    public void test_update_rating_success() throws Exception {
		this.mockMvc
		.perform(put(SERVICE_VERSION + "/1/rating/1").content(asJsonString(new Rating(95)))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
		.andDo(print()).andExpect(status().isOk()).andExpect(content().string(containsString(
				"{\"code\":4,\"message\":\"Student id = 1 updated the rating to 95 for the course id 1\",\"httpStatus\":\"OK\"}")));
    }
    
    @Test
    public void test_update_rating_student_not_found() throws Exception {
		this.mockMvc
		.perform(put(SERVICE_VERSION + "/300/rating/1").content(asJsonString(new Rating(95)))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
		.andDo(print()).andExpect(status().isNotFound()).andExpect(content().string(containsString(
				"{\"code\":10000,\"message\":\"Student id = 300 not found\",\"httpStatus\":\"NOT_FOUND\"}")));
    }
	
    @Test
    public void test_update_rating_course_not_found() throws Exception {
		this.mockMvc
		.perform(put(SERVICE_VERSION + "/1/rating/300").content(asJsonString(new Rating(95)))
				.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
		.andDo(print()).andExpect(status().isNotFound()).andExpect(content().string(containsString(
				"{\"code\":10001,\"message\":\"Course id = 300 not found\",\"httpStatus\":\"NOT_FOUND\"}")));
    }

	public static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
