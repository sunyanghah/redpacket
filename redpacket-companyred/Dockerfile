FROM java:8-jre
MAINTAINER redpacket-companyred

RUN cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo "Asia/shanghai" > /etc/timezone

ADD target/*.jar /app/redpacket-companyred.jar
CMD ["nohup", "java", "-jar", "/app/redpacket-companyred.jar", ">", "/dev/null", "&"]

EXPOSE 8002
