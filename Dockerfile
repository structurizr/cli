FROM openjdk:11

COPY build/distributions/structurizr-cli-*.zip /usr/local/structurizr-cli/
WORKDIR /usr/local/structurizr-cli/
RUN unzip /usr/local/structurizr-cli/structurizr-cli-*.zip
WORKDIR /usr/local/structurizr/

ENTRYPOINT ["java","-cp", "/usr/local/structurizr-cli:/usr/local/structurizr-cli/lib/*", "com.structurizr.cli.StructurizrCliApplication"]