FROM eclipse-temurin:8u352-b08-jre

WORKDIR /usr/local/structurizr-cli/
ENV PATH /usr/local/structurizr-cli/:$PATH
COPY build/distributions/structurizr-cli-*.zip ./
RUN apt-get update && apt-get install -y unzip
RUN unzip /usr/local/structurizr-cli/structurizr-cli-*.zip && chmod +x structurizr.sh
WORKDIR /usr/local/structurizr/

ENTRYPOINT ["structurizr.sh"]
