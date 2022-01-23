CREATE DATABASE IF NOT EXISTS COURSES_DB;

CREATE TABLE IF NOT EXISTS COURSES_DB.course(
id int NOT NULL AUTO_INCREMENT primary key,
name varchar(50) NOT NULL);

CREATE TABLE IF NOT EXISTS COURSES_DB.student(
id int NOT NULL AUTO_INCREMENT primary key,
name varchar(50) NOT NULL,
email varchar(50) NOT NULL);

CREATE TABLE IF NOT EXISTS COURSES_DB.course_student(
course_id int NOT NULL,
student_id int NOT NULL,
rating int NULL,
  PRIMARY KEY (course_id, student_id),
  FOREIGN KEY (course_id) REFERENCES COURSES_DB.course(id) ON DELETE CASCADE,
  FOREIGN KEY (student_id) REFERENCES COURSES_DB.student(id) ON DELETE CASCADE
);
