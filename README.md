# structurizr-publish

This is a command line utility to publish Markdown/AsciiDoc documentation and ADRs to a Structurizr workspace (the cloud service or an on-premises installation).

- __Documentation__: The tool will scan a given directory and automatically import all Markdown and AsciiDoc files in that directory, alphabetically according to the filename. Each file must represent a separate documentation section, and the second level heading (```## Section Title``` in Markdown and ```== Section Title``` in AsciiDoc) will be used as the section name.
- __ADRs__: The tool will scan a given directory and automatically import all Markdown and AsciiDoc files in that directory. The files must have been created by [adr-tools](https://github.com/npryce/adr-tools), or at least follow the same format.

The tool operates as follows:

1. Download the existing workspace.
2. Clear all existing documentation and decisions.
3. Import documentation and/or decisions into the workspace.
4. Upload the new version of the workspace. 

## Prerequisites

- You must have Java 8 or above installed, and available to use on your command line.
- Build the tool from the source, or download the [structurizr-publish-1.0.1.jar](https://github.com/structurizr/publish/releases/download/v1.0.1/structurizr-publish-1.0.1.jar) file.

## Usage

On the command line, in the same directory where you've placed the JAR file:

```
java -jar structurizr-publish-1.0.1.jar
```

## Parameters

- __-id__: the workspace ID (required)
- __-key__: the workspace API key (required)
- __-secret__: the workspace API secret (required)
- __-docs__: the path to the directory containing Markdown/AsciiDoc files to be published (optional)
- __-adrs__: the path to the directory containing ADRs (optional)
- __-url__: the Structurizr API URL (optional; defaults to https://api.structurizr.com)
- __-passphrase__: the passphrase to use (optional; only required if client-side encryption enabled on the workspace)

## Examples

To publish a directory of Markdown and/or AsciiDoc files as documentation:

```
java -jar structurizr-publish-1.0.1.jar -id 40120 -key 1a130d2b... -secret a9daaf3e... -docs example/docs
```

To publish a directory ADRs as decision records:

```
java -jar structurizr-publish-1.0.1.jar -id 40120 -key 1a130d2b... -secret a9daaf3e... -adrs example/adrs
```

The [example directory](https://github.com/structurizr/publish/tree/master/example)  has some example Markdown documentation and decision records. See [Financial Risk System - Documentation](https://structurizr.com/share/40120/documentation) and [Financial Risk System - Decisions](https://structurizr.com/share/40120/decisions) for the published version.