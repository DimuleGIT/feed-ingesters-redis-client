FROM openjdk:11.0.6-jdk
EXPOSE 8080
ADD target/feed-ingesters-redis-client-0.0.1-SNAPSHOT.jar app.jar
VOLUME /tmp
ENVIRONMENT
     WEB_APP: host.docker.internal
CMD ["java", "-jar", "/app.jar"]