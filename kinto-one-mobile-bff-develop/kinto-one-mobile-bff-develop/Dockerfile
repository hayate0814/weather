FROM amazoncorretto:17

VOLUME /tmp

ENV spring_profiles_active=server

COPY . ~/kinto-one-mobile-bff

WORKDIR ~/kinto-one-mobile-bff
RUN ./gradlew build -x test
RUN cp build/libs/kinto-one-mobile-bff-0.0.1-SNAPSHOT.jar ~/app-bff.jar

CMD java -jar ${JAVA_OPTS} ~/app-bff.jar
