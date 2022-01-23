package com.fromero.coursesapi.control;

import java.util.List;
import java.util.NoSuchElementException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fromero.coursesapi.error.ApiReturnMessage;
import com.fromero.coursesapi.error.ResponseMessageUtil;
import com.fromero.coursesapi.model.Course;
import com.fromero.coursesapi.service.CourseService;
import com.fromero.coursesapi.service.StudentService;

@RestController
@RequestMapping("/v1/courses")
public class CourseController {

	@Autowired
    private CourseService courseService;

	@Autowired
    private StudentService studentService;

    @GetMapping("/list")
    public List<Course> list() {
        return courseService.listAllCourses();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> get(@PathVariable Integer id) {
        try {
            Course Course = courseService.getCourse(id);
            return new ResponseEntity<Object>(Course, HttpStatus.OK);
        } catch (NoSuchElementException e) {
        	return ResponseMessageUtil.from(ApiReturnMessage.ERR_COURSE_NOT_FOUND, id);
        }
    }
    @PostMapping("/")
    public void add(@RequestBody Course Course) {
        courseService.saveCourse(Course);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody Course course, @PathVariable Integer id) {
        try {
        	// throws NoSuchElementException if the student does not exist
            courseService.getCourse(id); 
            course.setId(id);
            courseService.saveCourse(course);
            return ResponseMessageUtil.from(ApiReturnMessage.COURSE_UPDATED, id);
        } catch (NoSuchElementException e) {
        	return ResponseMessageUtil.from(ApiReturnMessage.ERR_COURSE_NOT_FOUND, id);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
    	try {
        	// throws NoSuchElementException if the student does not exist
    		courseService.getCourse(id); 
    		courseService.deleteCourse(id);
            return ResponseMessageUtil.from(ApiReturnMessage.COURSE_DELETED, id);
        } catch (NoSuchElementException e) {
        	return ResponseMessageUtil.from(ApiReturnMessage.ERR_COURSE_NOT_FOUND, id);
        }    	
    }

    @GetMapping("/{id}/list-students")
    public Object listStudents(@PathVariable Integer id) {
        try {
        	// throws NoSuchElementException if the student does not exist
            courseService.getCourse(id);
            return studentService.listAllStudentsByCourse(id);
        } catch (NoSuchElementException e) {
        	return ResponseMessageUtil.from(ApiReturnMessage.ERR_COURSE_NOT_FOUND, id);
        }
    }
    
    @GetMapping("/list-no-students")
    public List<Course> listNoStudents() {
        return courseService.listAllCoursesWithoutStudents();
    }
    
}
