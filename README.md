# SPRINGBOOT REST EXAMPLE WITH MYSQL, DOCKER, DOCKER COMPOSE 

## Description
This is a simple project to illustrate a REST API using SpringBoot, MySQL, Docker and Docker Compose.
The project includes:
- A API to register students and courses.
- Docker compose to easily start the api in any OS, local or cloud.
- SpringBoot Rest + JPA + Maven + Lombok + Spring Doc + MySQL.
- The scripts schema.sql and data.sql to initialize the container of mysql.
- Unit tests using JUnit and mocking the repository.
- Folders to illustrate different environments: dev, qa, prod, local-mysql.

The project does not include (but can be developed easily):
- Security (Oath2 - Token). You can use spring security.
- Cache. You can use Spring cache with Redis, Memcache, etc.
- Load balance. You can use nginx or apache to load balance

## Prerequisites
- Git client installed: https://git-scm.com/downloads
- Docker installed: https://www.docker.com/products/docker-desktop
- For local development you need Java 8+, Maven and your favorite IDE.

## Starting up the API

In order to start the application run:

### Clone the repository and run docker compose:
```
git clone https://github.com/fgalha/springboot-rest-docker-mysql.git
docker compose -f springboot-rest-docker-mysql/dev/docker-compose.yml up
```

###### For windows use:
```
docker compose -f springboot-rest-docker-mysql\dev\docker-compose.yml up
```

## API DOCS
You can see the API documentation via:
http://localhost:8080/v1/api-doc.html




