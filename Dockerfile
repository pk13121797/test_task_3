FROM tomcat:10.1.19-jdk21-temurin-jammy
ADD ./target/test_task.war /usr/local/tomcat/webapps/ROOT.war
RUN mkdir app_logs
EXPOSE 8080
CMD ["catalina.sh", "run"]