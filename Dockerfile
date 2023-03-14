FROM eclipse-temurin:17-jre-jammy

WORKDIR /usr/local/structurizr-cli/
ENV PATH /usr/local/structurizr-cli/:$PATH
COPY build/distributions/structurizr-cli-*.zip ./
RUN apt-get update && apt-get install -y unzip && rm -rf /var/lib/apt/lists/*
RUN unzip /usr/local/structurizr-cli/structurizr-cli-*.zip && chmod +x structurizr.sh
WORKDIR /usr/local/structurizr/

ENTRYPOINT ["structurizr.sh"]
