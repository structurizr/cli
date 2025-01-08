FROM eclipse-temurin:21.0.5_11-jre-noble

RUN apt-get update && apt-get install -y unzip && apt install -y graphviz && rm -rf /var/lib/apt/lists/*

COPY build/distributions/structurizr-cli.zip /tmp

RUN unzip /tmp/structurizr-cli.zip -d /usr/local/structurizr-cli && chmod +x /usr/local/structurizr-cli/structurizr.sh

WORKDIR /usr/local/structurizr

ENTRYPOINT ["/usr/local/structurizr-cli/structurizr.sh"]