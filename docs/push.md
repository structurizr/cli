# push

The ```push``` command allows you to push content to a Structurizr workspace (the cloud service or an on-premises installation). The command operates as follows:

1. Pull the existing workspace, OR parse the specified DSL file to create a new workspace.
2. Clear existing documentation and decisions from the workspace.
3. Import documentation and/or decisions into the workspace.
4. Push the workspace. 

## Options

- __-id__: The workspace ID (required)
- __-key__: The workspace API key (required)
- __-secret__: The workspace API secret (required)
- __-workspace__: The path to the workspace JSON file/DSL file(s) (required)
- __-docs__: The path to the directory containing Markdown/AsciiDoc files to be published (optional)
- __-adrs__: The path to the directory containing ADRs (optional)
- __-url__: The Structurizr API URL (optional; defaults to ```https://api.structurizr.com```)
- __-passphrase__: The passphrase to use (optional; only required if client-side encryption enabled on the workspace)

## Documentation

When the ```-docs``` option is used,  the ```push``` command will import all Markdown and AsciiDoc files in this directory, alphabetically according to the filename. Each file must represent a separate documentation section, and the second level heading (```## Section Title``` in Markdown and ```== Section Title``` in AsciiDoc) will be used as the section name.

## Architecture Decision Records (ADRs)

When the ```-adrs``` option is used, the ```push``` command will import all Markdown and AsciiDoc files in this directory. The files must have been created by [adr-tools](https://github.com/npryce/adr-tools), or at least follow the same format.

## Examples

To push a directory of Markdown and/or AsciiDoc files as documentation to an existing Structurizr workspace:

```
java -jar structurizr-cli-*.jar push -id 40120 -key 1a130d2b... -secret a9daaf3e... -docs docs
```

To push a directory of ADRs as decision records to an existing Structurizr workspace:

```
java -jar structurizr-cli-*.jar push -id 40120 -key 1a130d2b... -secret a9daaf3e... -adrs adrs
```