ARG GRADLE_VERSION='6.3'

FROM gradle:${GRADLE_VERSION}-jdk8 as build

WORKDIR /src
COPY . .
USER root

ARG GRADLE_BUILD_COMMAND="bootJar"
RUN gradle test --no-daemon --console plain
RUN gradle ${GRADLE_BUILD_COMMAND} --refresh-dependencies --no-daemon --console plain
RUN find . -name '*.jar'

FROM openjdk:8-jre-alpine as run

WORKDIR /
COPY --from=build /src/build/libs/structurizr-cli-1.6.0.jar /src/lib/structurizr-dsl-1.0.0.jar ./
RUN mkdir -p /workdir
WORKDIR /workdir

ENTRYPOINT ["java", "-jar", "/structurizr-cli-1.6.0.jar" ]
