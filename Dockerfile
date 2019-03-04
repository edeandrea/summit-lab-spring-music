#FROM registry.access.redhat.com/openjdk/openjdk-11-rhel7

# Multi-layer
#FROM adoptopenjdk/openjdk11
FROM registry.access.redhat.com/openjdk/openjdk-11-rhel7
VOLUME /tmp
ARG DEPENDENCY=build/dependency
COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","io.k8s.samples.music.Application"]

# FOR simple run
#VOLUME /tmp
#COPY build/libs/*.jar app.jar
#ENTRYPOINT ["java","-jar","/app.jar"]

# Multi-stage
#FROM adoptopenjdk/openjdk11 as build
#WORKDIR /workspace/app
#
#COPY gradlew .
#COPY build.gradle .
#COPY gradle gradle
#COPY .git .git
#COPY src src
#
#RUN ./gradlew clean assemble
#RUN mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*.jar)
#
#FROM adoptopenjdk/openjdk11
#VOLUME /tmp
#ARG DEPENDENCY=/workspace/app/build/dependency
#COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
#COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
#COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
#ENTRYPOINT ["java","-cp","app:app/lib/*","io.k8s.samples.music.Application"]
