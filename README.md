# structurizr-publish

This is a command line utility to publish Markdown/AsciiDoc documentation and ADRs (created by [adr-tools](https://github.com/npryce/adr-tools)) to a Structurizr workspace (the cloud service or an on-premises installation).

## Prerequisites

- You must have Java 8 or above installed, and available to use on your command line.
- Build the tool from the source, or download the [structurizr-publish-1.0.0.jar](https://github.com/structurizr/publish/releases/download/v1.0.0/structurizr-publish-1.0.0.jar) file.

## Usage

On the command line, in the same directory where you've placed the JAR file:

```
java -jar structurizr-publish-1.0.0.jar
```

## Parameters

- __-id__: the workspace ID (required)
- __-key__: the workspace API key (required)
- __-secret__: the workspace API secret (required)
- __-docs__: the path to the directory containing Markdown/AsciiDoc files to be published (optional)
- __-adrs__: the path to the directory containing ADRs (optional)
- __-url__: the Structurizr API URL (optional; defaults to https://api.structurizr.com)

## Examples

To publish a directory of Markdown and/or AsciiDoc files as documentation:

```
java -jar structurizr-publish-1.0.0.jar -id 40120 -key 1a130d2b... -secret a9daaf3e... -docs example/docs
```

To publish a directory ADRs as decision records:

```
java -jar structurizr-publish-1.0.0.jar -id 40120 -key 1a130d2b... -secret a9daaf3e... -adrs example/adrs
```

The [example directory](https://github.com/structurizr/publish/tree/master/example)  has some example Markdown documentation and decision records. See [Financial Risk System - Documentation](https://structurizr.com/share/40120/documentation) and [Financial Risk System - Decisions](https://structurizr.com/share/40120/decisions) for the published version.