# structurizr-cli

This is a command line utility for Structurizr, and supports the following functionality:

- Push content to a Structurizr workspace (the cloud service or an on-premises installation)
	- A model and views defined using the [Structurizr DSL](https://github.com/structurizr/dsl)
	- Markdown/AsciiDoc documentation
	- Architecture Decision Records (ADRs)
- Pull the workspace as JSON
- Export diagrams to PlantUML, and WebSequenceDiagrams
	
## Prerequisites

- You must have Java 8 or above installed, and available to use on your command line.
- Build the tool from the source, or download the [structurizr-cli](https://github.com/structurizr/cli/releases) ZIP file.
 - To use the ```push``` and ```pull``` commands, you need a Structurizr workspace, and the following information from your dashboard (see [Help - Workspaces](https://structurizr.com/help/workspaces) for details):
    - Workspace ID
    - API key
    - API secret

## Usage

On the command line, in the same directory where you've placed the JAR file:

```
java -jar structurizr-cli-*.jar <command> [options]
```

Supported commands are:

- ```push```
- ```pull```
- ```export```

## push

The ```push``` command allows you to push content to a Structurizr workspace (the cloud service or an on-premises installation). The command operates as follows:

1. Pull the existing workspace, or parse the DSL file to create a new workspace.
2. Clear existing documentation and decisions.
3. Import documentation and/or decisions into the workspace.
4. Push the workspace. 

### Options

- __-id__: The workspace ID (required).
- __-key__: The workspace API key (required).
- __-secret__: The workspace API secret (required).
- __-workspace__: The path to the file or directory containing a definition of the workspace (model and views) in the Structurizr DSL format.
- __-docs__: The path to the directory containing Markdown/AsciiDoc files to be published (optional). The tool will import all Markdown and AsciiDoc files in this directory, alphabetically according to the filename. Each file must represent a separate documentation section, and the second level heading (```## Section Title``` in Markdown and ```== Section Title``` in AsciiDoc) will be used as the section name.
- __-adrs__: The path to the directory containing ADRs (optional). The tool will import all Markdown and AsciiDoc files in this directory. The files must have been created by [adr-tools](https://github.com/npryce/adr-tools), or at least follow the same format.
- __-url__: The Structurizr API URL (optional; defaults to ```https://api.structurizr.com```).
- __-passphrase__: The passphrase to use (optional; only required if client-side encryption enabled on the workspace).

## pull

The ```pull``` command allows you to pull content from a Structurizr workspace (the cloud service or an on-premises installation), as a JSON document.

### Options

- __-id__: The workspace ID (required).
- __-key__: The workspace API key (required).
- __-secret__: The workspace API secret (required).
- __-url__: The Structurizr API URL (optional; defaults to ```https://api.structurizr.com```).

## export

The ```export``` command allows you to export the views within a Structurizr workspace to a number of different formats.

### Options

- __-workspace__: The path to the workspace JSON file (required).
- __-format__: plantuml|websequencediagrams (required).

## Examples

To push a directory of Markdown and/or AsciiDoc files as documentation to a Structurizr workspace:

```
java -jar structurizr-cli-*.jar push -id 40120 -key 1a130d2b... -secret a9daaf3e... -docs example/docs
```

To push a directory ADRs as decision records to a Structurizr workspace:

```
java -jar structurizr-cli-*.jar push -id 40120 -key 1a130d2b... -secret a9daaf3e... -adrs example/adrs
```

The [example directory](https://github.com/structurizr/publish/tree/master/example)  has some example Markdown documentation and decision records. See [Financial Risk System - Documentation](https://structurizr.com/share/40120/documentation) and [Financial Risk System - Decisions](https://structurizr.com/share/40120/decisions) for the published version.