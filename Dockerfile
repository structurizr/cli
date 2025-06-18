FROM eclipse-temurin:21.0.7_6-jre-noble

RUN apt-get update && apt-get install -y libarchive-tools && apt install -y graphviz && rm -rf /var/lib/apt/lists/*

COPY build/distributions/structurizr-cli.zip /tmp

ARG APPDIR=/usr/local/structurizr-cli

RUN <<EOF
mkdir -p $APPDIR
bsdtar -xf /tmp/structurizr-cli.zip -C $APPDIR --strip-components 1
EOF

WORKDIR "$APPDIR"

ENTRYPOINT ["bin/structurizr-cli"]
