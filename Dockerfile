 FROM java:8
 MAINTAINER mandy
 ADD netty-0.0.1-SNAPSHOT.jar spring_boot.jar
 EXPOSE 8080
 ENTRYPOINT ["java","-jar","spring_boot.jar"]
