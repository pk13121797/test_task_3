# REST service for storing information about places of interest.

Rest service allows crud operations with localities and attractions.
Sorting, filtering, logging, migrations, validation, i18n, caching have also been added. 
The entire code is covered by integration and unit tests.
Integration with an external REST weather service is also implemented.

Technologies:

1) Spring MVC
2) Spring Data Jpa
3) Liquibase 
4) Postgres
5) Junit & Mockito & AssertJ 
6) Testcontainers 
7) Log4j 
8) Docker

Instructions to get the application up and running:

1) Make sure docker is installed and running.
2) Clone this repository.
3) Build the application war (Run mvn install or use IntelliJ IDEA to build the war file).
4) Check that port 5432 is free
5) Run docker-compose up. Give the application a few seconds to come up.
6) Access the application at http://localhost:8080
