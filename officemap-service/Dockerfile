FROM java:8-jre
MAINTAINER Valeriy Doroshenko <vdoroshenko@gmail.com>

ADD ./target/officemap-service.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/officemap-service.jar"]

EXPOSE 6000