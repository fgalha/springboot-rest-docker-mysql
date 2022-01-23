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
import com.fromero.coursesapi.errorhandling.BusinessErrorException;
import com.fromero.coursesapi.model.Course;
import com.fromero.coursesapi.model.Student;
import com.fromero.coursesapi.service.CourseService;
import com.fromero.coursesapi.service.StudentService;

@RestController
@RequestMapping("/v1/students")
public class StudentController {

	@Autowired
    private StudentService studentService;

	@Autowired
    private CourseService courseService;

    @GetMapping("/list")
    public List<Student> list() {
        return studentService.listAllStudents();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> get(@PathVariable Integer id) {
        try {
            Student Student = studentService.getStudent(id);
            return new ResponseEntity<Object>(Student, HttpStatus.OK);
        } catch (NoSuchElementException e) {
        	return ResponseMessageUtil.from(ApiReturnMessage.ERR_STUDENT_NOT_FOUND, id);
        }
    }
    @PostMapping("/")
    public void add(@RequestBody Student student) {
        studentService.saveStudent(student);
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody Student student, @PathVariable Integer id) {
        try {
        	// throws NoSuchElementException if the student does not exist
            studentService.getStudent(id); 
            student.setId(id);
            studentService.saveStudent(student);
            return ResponseMessageUtil.from(ApiReturnMessage.STUDENT_UPDATED, id);
        } catch (NoSuchElementException e) {
        	return ResponseMessageUtil.from(ApiReturnMessage.ERR_STUDENT_NOT_FOUND, id);
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
    	try {
        	// throws NoSuchElementException if the student does not exist
            studentService.getStudent(id); 
            studentService.deleteStudent(id);
            return ResponseMessageUtil.from(ApiReturnMessage.STUDENT_DELETED, id);
        } catch (NoSuchElementException e) {
        	return ResponseMessageUtil.from(ApiReturnMessage.ERR_STUDENT_NOT_FOUND, id);
        }    	
    }
    
    @PostMapping("/{idstudent}/register/{idcourse}")
    public ResponseEntity<?> registerCourse(@PathVariable Integer idstudent, @PathVariable Integer idcourse) {
    	Student student;
    	Course course;
    	try {
    		student = studentService.getStudent(idstudent);
    	} catch (NoSuchElementException e) {
    		return ResponseMessageUtil.from(ApiReturnMessage.ERR_STUDENT_NOT_FOUND, idstudent);
    	}
    	try {
    		course = courseService.getCourse(idcourse);
    	} catch (NoSuchElementException e) {
    		return ResponseMessageUtil.from(ApiReturnMessage.ERR_COURSE_NOT_FOUND, idcourse);
    	}
    	try {
    		studentService.registerCourse(student, course);
    	} catch (BusinessErrorException e) {
    		return ResponseMessageUtil.from(e.getApiReturnMessage(), e.getArgs());
    	}
    	return ResponseMessageUtil.from(ApiReturnMessage.STUDENT_ENROLLED_TO_COURSE, idstudent, idcourse);
    }

    @PostMapping("/{idstudent}/unregister/{idcourse}")
    public ResponseEntity<?> unregister(@PathVariable Integer idstudent, @PathVariable Integer idcourse) {
    	Student student;
    	Course course;
    	try {
    		student = studentService.getStudent(idstudent);
    	} catch (NoSuchElementException e) {
    		return ResponseMessageUtil.from(ApiReturnMessage.ERR_STUDENT_NOT_FOUND, idstudent);
    	}
    	try {
    		course = courseService.getCourse(idcourse);
    	} catch (NoSuchElementException e) {
    		return ResponseMessageUtil.from(ApiReturnMessage.ERR_COURSE_NOT_FOUND, idcourse);
    	}
    	try {
    		studentService.unregisterCourse(student, course);
    	} catch (BusinessErrorException e) {
    		return ResponseMessageUtil.from(e.getApiReturnMessage(), e.getArgs());
    	}
    	return ResponseMessageUtil.from(ApiReturnMessage.STUDENT_UNREGISTER_TO_COURSE, idstudent, idcourse);
    }

    @GetMapping("/{id}/list-courses")
    public Object listCourses(@PathVariable Integer id) {
        try {
        	// throws NoSuchElementException if the student does not exist
            studentService.getStudent(id);
            return courseService.listAllCoursesByStudent(id);
        } catch (NoSuchElementException e) {
        	return ResponseMessageUtil.from(ApiReturnMessage.ERR_STUDENT_NOT_FOUND, id);
        }
    }

    @GetMapping("/list-no-courses")
    public List<Student> listNoCourses() {
        return studentService.listAllStudentsWithoutCourses();
    }

}
