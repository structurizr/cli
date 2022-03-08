# push

The ```push``` command allows you to push the specified DSL/JSON file to a Structurizr workspace (the cloud service or an on-premises installation).

## Options

- __-id__: The workspace ID (required)
- __-key__: The workspace API key (required)
- __-secret__: The workspace API secret (required)
- __-workspace__: The path to the workspace JSON file/DSL file(s) (required)
- __-url__: The Structurizr API URL (optional; defaults to ```https://api.structurizr.com```)
- __-passphrase__: The passphrase to use (optional; only required if client-side encryption enabled on the workspace)
- __-merge__: Whether to merge layout information from the remote workspace (optional; defaults to `true`)
- __-archive__: Whether to store the previous version of the remote workspace (optional; default to `true`)

## Examples

To push a new version of a workspace defined using the DSL:

```
./structurizr.sh push -id 123456 -key 1a130d2b... -secret a9daaf3e... -workspace workspace.dsl
```