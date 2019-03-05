FROM adoptopenjdk/openjdk11:alpine-slim

ARG VERSION
WORKDIR /usr/feaggle

ENV FEAGGLE_JDBC_URL "_REQUIRED_"
ENV FEAGGLE_JDBC_USER "_REQUIRED_"
ENV FEAGGLE_JDBC_PASSWORD "_REQUIRED_"
ENV FEAGGLE_PORT 9092

ADD build/libs/server-${VERSION}.jar server.jar
CMD [ "java", "-jar", "server.jar" ]