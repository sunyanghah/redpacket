FROM java:8-jre
MAINTAINER redpacket-countred

RUN cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo "Asia/shanghai" > /etc/timezone

ADD target/*.jar /app/redpacket-countred.jar
CMD ["nohup", "java", "-jar", "/app/redpacket-countred.jar", ">", "/dev/null", "&"]

EXPOSE 8003
