FROM openjdk:17

WORKDIR /usr/local/structurizr-cli/
ENV PATH /usr/local/structurizr-cli/:$PATH
COPY build/distributions/structurizr-cli-*.zip ./
RUN jar xf structurizr-cli-*.zip && chmod +x structurizr.sh
WORKDIR /usr/local/structurizr/

ENTRYPOINT ["structurizr.sh"]
