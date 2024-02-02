#Start with th base image containing java runtime
FROM openjdk:17-jdk-slim

#Inofrmation around who maintain the image
MAINTAINER isalmanportfolio.netlify.app

#Add application's jar to image
COPY target/accounts-0.0.1-SNAPSHOT.jar accounts-0.0.1-SNAPSHOT.jar

#Execute the application
ENTRYPOINT ["java","-jar","accounts-0.0.1-SNAPSHOT.jar"]