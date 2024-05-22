# TaskManagementSystem

Just simple task management system. The system allows you to manage tasks, employees and projects.
There is a possibility of registration, authorization and authentication.
Sorting, filtering, pagination, validation, i18n, logging, file uploading, mail sending, 
changing the password have also been added. The entire code is covered by integration and unit tests.

Technologies:

1) Spring MVC
2) Spring Data Jpa
3) Spring Security
4) Spring Aop 
5) Postgres & H2 
6) Thymeleaf & Bootstrap 
7) Junit & Mockito & AssertJ 
8) Testcontainers 
9) Log4j 
10) Oauth2 
11) Docker

Instructions to get the application up and running:

1) Make sure docker is installed and running.
2) Clone this repository.
3) Build the application war (Run mvn install or use IntelliJ IDEA to build the war file).
4) Run docker-compose up. Give the application a few seconds to come up. 
5) Access the application at http://localhost:8080

Test data for login:

1) ADMIN login and password: heleg14940@dovesilo.com
2) USER login and password: conatag114@dovesilo.com
