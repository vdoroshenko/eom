FROM java:8-jre
MAINTAINER Valeriy Doroshenko <vdoroshenko@gmail.com>

ADD ./target/cms-service.jar /app/
ADD ./data/images.zip/ /store/
CMD ["java", "-Xmx200m", "-jar", "/app/cms-service.jar"]

EXPOSE 6000