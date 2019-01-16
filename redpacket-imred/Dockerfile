FROM java:8-jre
MAINTAINER redpacket-imred

RUN cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo "Asia/shanghai" > /etc/timezone

ADD target/*.jar /app/redpacket-imred.jar
CMD ["nohup", "java", "-jar", "/app/redpacket-imred.jar", ">", "/dev/null", "&"]

EXPOSE 8001
