FROM java:8-jre
MAINTAINER redpacket-companyred

RUN cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo "Asia/shanghai" > /etc/timezone

CMD ["nohup", "java", "-jar", "/app/redpacket-companyred-1.0-SNAPSHOT.jar", ">", "/dev/null", "&"]

EXPOSE 8002
