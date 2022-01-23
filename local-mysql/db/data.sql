-- CLEAR THE TABLES
DELETE FROM COURSES_DB.course_student;
DELETE FROM COURSES_DB.student;
DELETE FROM COURSES_DB.course;
-- RESET THE AUTO_INCREMENT (depends on the privilegies, I assume we have it)
ALTER TABLE COURSES_DB.student AUTO_INCREMENT = 1;
ALTER TABLE COURSES_DB.course AUTO_INCREMENT = 1;
-- INSERT DATA
INSERT INTO COURSES_DB.course(name)
VALUES
('Algorithms I'),
('Algorithms II'),
('Operational Systems'),
('Software Engineering'),
('Machine Learning I'),
('Machine Learning II'),
('Database I'),
('Database II'),
('Calculations I')
;
INSERT INTO COURSES_DB.student(name, email)
VALUES
  ('John Smith', 'john.smith@abc123.com'),
  ('Maria Silva', 'maria.silva@abc123.com'),
  ('Michael Scott', 'michael.scott@abc123.com'),
  ('Vicente Kross', 'vicente.kross@abc123.com'),
  ('Erik Sollenman', 'erik.s@abc123.com')
;
INSERT INTO COURSES_DB.course_student(
  course_id,
  student_id,
  rating)
VALUES
  (1,1,85),
  (1,2,82),
  (1,3,77),
  (1,4,96),
  (1,5,55),
  (3,1,77),
  (4,2,49),
  (5,2,82)
;
