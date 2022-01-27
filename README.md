# SPRINGBOOT REST EXAMPLE WITH MYSQL, DOCKER, DOCKER COMPOSE 

## Description
This is a simple project to illustrate a REST API using SpringBoot, MySQL, Docker and Docker Compose.
The project includes:
- A API to register students and courses.
- Docker compose to easily start the api in any OS, local or cloud.
- SpringBoot Rest + JPA + Maven + Lombok + Spring Doc + MySQL.
- The scripts schema.sql and data.sql to initialize the container of mysql.
- Unit tests using JUnit, SprintBoot Test and Mockito.
- Folders to illustrate different environments: dev, qa, prod, local-mysql.

The project does not include (but can be developed easily):
- Integration tests. I consider it is strictly necessary in a company environment and can be developed creating integration test environment, backfilling data and using some other development to send http messages to api and validating the response.
- Security (Oath2 - Token). You can use spring security.
- Cache. You can use Spring cache with Redis, Memcache, etc.
- Load balance. You can use nginx or apache to load balance.

## Prerequisites
- Git client installed: https://git-scm.com/downloads
- Docker installed: https://www.docker.com/products/docker-desktop
- For local development you need Java 8+, Maven and your favorite IDE.

## Starting up the API

In order to start the application run:

### Clone the repository and run docker compose (remove -d option to see console attached):
```
git clone https://github.com/fgalha/springboot-rest-docker-mysql.git
docker compose -f springboot-rest-docker-mysql/dev/docker-compose.yml up -d
```

###### For windows use:
```
docker compose -f springboot-rest-docker-mysql\dev\docker-compose.yml up -d
```

## LOCAL TESTS
You can run in your local machine. After clone repository, start the local mysql by the command:
```
docker compose -f springboot-rest-docker-mysql/local-mysql/docker-compose.yml up
```

Via your favorite Java IDE:
```
Run the class CoursesApiApplication passing to Program Arguments:
--spring.profiles.active=local
```

Via command line:
```
cd springboot-rest-docker-mysql/courses-rest-services
mvn spring-boot:run -Dspring-boot.run.profiles=local
```


## API DOCS
You can see the API documentation via:
http://localhost:8080/v1/api-doc.html

## API DESCRIPTION
The api is pretty self explanatory, but you can run some tests using curl, Postman or any http client. See the full list of curl examples:

###### list students
curl -X GET http://localhost:8080/v1/students/list

###### insert a student
curl -d '{"name": "Luke Skywalker","email": "luke.skywalker@test.com"}' -H "Content-Type: application/json" -X POST http://localhost:8080/v1/students/

###### update a student
curl -d '{"name": "Paul McCartney","email": "paul.mcc@test.com"}' -H "Content-Type: application/json" -X PUT http://localhost:8080/v1/students/1

###### delete a student
curl -X DELETE http://localhost:8080/v1/students/2

###### student information
curl -X GET http://localhost:8080/v1/students/1

###### list courses of the student
curl -X GET http://localhost:8080/v1/students/1/list-courses/

###### list courses of the student
curl -X GET http://localhost:8080/v1/students/1/list-courses/

###### list students without a course
curl -X GET http://localhost:8080/v1/students/list-no-courses

###### update the rating of a student's course, the first id is student, the second is the course's id.
curl -d '{"value":44}' -H "Content-Type: application/json" -X PUT http://localhost:8080/v1/students/1/rating/1

###### register a student to a course, the first id is student, the second is the course's id.
curl -X POST http://localhost:8080/v1/students/1/register/5

###### unregister a student to a course, the first id is student, the second is the course's id.
curl -X POST http://localhost:8080/v1/students/1/unregister/5

###### list all courses
curl -X GET http://localhost:8080/v1/courses/list

###### list a course
curl -X GET http://localhost:8080/v1/courses/1

###### insert a course
curl -d '{"name": "SpringBoot in a glove"}' -H "Content-Type: application/json" -X POST http://localhost:8080/v1/courses/

###### update a course
curl -d '{"name": "Algorithms I for fast learners"}' -H "Content-Type: application/json" -X PUT http://localhost:8080/v1/courses/1/

###### delete a course
curl -X DELETE http://localhost:8080/v1/courses/5

###### list students of a course
curl -X GET http://localhost:8080/v1/courses/1/list-students/

###### list students with no courses enrolled
curl -X GET http://localhost:8080/v1/courses/list-no-students/
