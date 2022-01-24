package com.fromero.coursesapi.service;

import java.io.Serializable;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Streamable;
import org.springframework.stereotype.Service;

import com.fromero.coursesapi.model.Course;
import com.fromero.coursesapi.repository.CourseRepository;

@Service
@Transactional
public class CourseService implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Autowired
    private CourseRepository courseRepository;
    
    public List<Course> listAllCourses() {
        return Streamable.of(courseRepository.findAll()).toList();
    }

    public Course saveCourse(Course course) {
    	return courseRepository.save(course);
    }

    public Course getCourse(Integer id) {
        return courseRepository.findById(id).get();
    }

    public void deleteCourse(Integer id) {
    	courseRepository.deleteById(id);
    }

	public List<Course> listAllCoursesByStudent(Integer id) {
		return courseRepository.listCoursesByStudent(id);
	}

	public List<Course> listAllCoursesWithoutStudents() {
		return courseRepository.listAllCoursesWithoutStudents();
	}
}
