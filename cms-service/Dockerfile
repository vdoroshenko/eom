FROM java:8-jre
MAINTAINER Valeriy Doroshenko <vdoroshenko@gmail.com>

ADD ./target/cms-service.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/cms-service.jar"]

EXPOSE 6000