# Docker Based CLI

You many not have *java* installed or properly configured to run the *structurizr cli* on your system. A *Docker* based version provides a prebuilt version of the *cli* that you can run. A *shall script* is provided to make this easy.

## Installation

Assuming that you have docker installed on your workstation (it can be downloaded [here](https://www.docker.com/products/docker-desktop))...

On *Mac OS* or your *Linux* Copy* `docker/structurizr` to your execution path and make it executable.

Once installed, `structurizr` works as if you followed the *java based instructions* with the exception that any of the files or directories that you reference must be found under your *current working directory*. If you look at the shell script, you will note that this is because your *current working directory* is mounted into the *docker container*.

Feel free to adapt the `structurizr` script to your own needs; it is simple.

## Building

If you would like to build the image yourself

```bash
docker build --rm -t myregistry/structurizr-cli -f docker/Dockerfile .
```

Then update `structurizr` to set `REGISTRY` to `myregistry`.
