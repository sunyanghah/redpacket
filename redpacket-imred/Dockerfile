FROM java:8-jre
MAINTAINER redpacket-imred

RUN cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo "Asia/shanghai" > /etc/timezone

CMD ["nohup", "java", "-jar", "/app/*.jar", ">", "/dev/null", "&"]

EXPOSE 8001
