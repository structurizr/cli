![Structurizr](docs/images/structurizr-banner.png)

# Structurizr command line interface (CLI)

This GitHub repository contains the Structurizr CLI - a command line utility for Structurizr, and supports the following functionality:

- __Push__ content to a Structurizr workspace (the cloud service or an on-premises installation)
	- A model and views defined using the [Structurizr DSL](https://github.com/structurizr/dsl)
	- Markdown/AsciiDoc documentation
	- Architecture Decision Records (ADRs)
- __Pull__ workspace content as JSON
- __Export__ diagrams to PlantUML, Mermaid, and WebSequenceDiagrams

__This repository is supported by Structurizr Limited__, as a part of the Structurizr service.
	
## Prerequisites

- You must have Java 8 or above installed, and available to use on your command line.
- Build the tool from the source (```gradlew build```), or download the prebuilt [structurizr-cli](https://github.com/structurizr/cli/releases) ZIP file.
 - To use the ```push``` and ```pull``` commands, you need a Structurizr workspace, and the following information from your dashboard (see [Help - Workspaces](https://structurizr.com/help/workspaces) for details):
    - Workspace ID
    - API key
    - API secret

## Getting started

For a quick tutorial, see the [getting started](docs/getting-started.md) guide. 

## Usage

On the command line, in the same directory where you've placed the JAR file:

### macOS/Linux

```
./structurizr.sh <command> [options]
```

### Windows

```
structurizr <command> [options]
```

Supported commands are:

- [push](docs/push.md)
- [pull](docs/pull.md)
- [export](docs/export.md)

The Structurizr CLI will create some default views and styles for you if you don't define them yourself. See [Defaults](docs/defaults.md) for more details.