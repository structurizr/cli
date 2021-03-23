# push

The ```push``` command allows you to push content to a Structurizr workspace (the cloud service or an on-premises installation). The command operates as follows:

1. Create a workspace to operate on:
	1. If `-workspace` is specified, the CLI will parse the specified DSL/JSON file.
	2. If `-workspace` is not specified, the CLI will pull the existing version of the workspace via the web API, removing any existing documentation and decisions.
3. If `-docs` is specified, that documentation is added to the workspace.
4. If `-adrs` is specified, those decisions are added to the workspace.
4. Push the workspace via the web API.

## Options

- __-id__: The workspace ID (required)
- __-key__: The workspace API key (required)
- __-secret__: The workspace API secret (required)
- __-workspace__: The path to the workspace JSON file/DSL file(s) (required)
- __-docs__: The path to the directory containing Markdown/AsciiDoc files to be published (optional)
- __-adrs__: The path to the directory containing ADRs (optional)
- __-url__: The Structurizr API URL (optional; defaults to ```https://api.structurizr.com```)
- __-passphrase__: The passphrase to use (optional; only required if client-side encryption enabled on the workspace)
- __-merge__: Whether to merge layout information from the remote workspace (optional; defaults to `true`)

## Documentation

When the ```-docs``` option is used,  the ```push``` command will import all Markdown and AsciiDoc files in this directory (and sub-directories), alphabetically according to the filename. Each file must represent a separate documentation section, and the second level heading (```## Section Title``` in Markdown and ```== Section Title``` in AsciiDoc) will be used as the section name.

## Architecture Decision Records (ADRs)

When the ```-adrs``` option is used, the ```push``` command will import all Markdown and AsciiDoc files in this directory. The files must have been created by [adr-tools](https://github.com/npryce/adr-tools), or at least follow the same format.

## Examples

To push a new version of a workspace defined using the DSL:

```
./structurizr.sh push -id 123456 -key 1a130d2b... -secret a9daaf3e... -workspace workspace.dsl
```


To push a directory of Markdown and/or AsciiDoc files as documentation to an existing Structurizr workspace:

```
./structurizr.sh push -id 123456 -key 1a130d2b... -secret a9daaf3e... -docs docs
```

To push a directory of ADRs as decision records to an existing Structurizr workspace:

```
./structurizr.sh push -id 123456 -key 1a130d2b... -secret a9daaf3e... -adrs adrs
```