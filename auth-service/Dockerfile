FROM java:8-jre
MAINTAINER Valeriy Doroshenko <vdoroshenko@gmail.com>

ADD ./target/auth-service.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/auth-service.jar"]

EXPOSE 5000