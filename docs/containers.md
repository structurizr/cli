# Pre-built containers (Docker, dev containers, etc)

A prebuilt Docker image is available at Docker Hub. To use it, for example:

```
docker pull structurizr/cli:latest
docker run -it --rm -v $PWD:/usr/local/structurizr structurizr/cli <parameters>
```

In this example, `$PWD` will mount the current local directory as the CLI working directory (`/usr/local/structurizr` in the Docker container).

Alternative containers are available via:

- [leopoldodonnell/structurizr-cli-docker](https://github.com/leopoldodonnell/structurizr-cli-docker)
- [aidmax/structurizr-cli-docker](https://github.com/aidmax/structurizr-cli-docker)
- [evilpilaf/structurizr-remotecontainer](https://github.com/evilpilaf/structurizr-remotecontainer)
