# pull

The ```pull``` command allows you to pull content from a Structurizr workspace (the cloud service or an on-premises installation), as a JSON document. A file will created with the name ```structurizr-<id>-workspace.json``` in the current directory.

## Options

- __-id__: The workspace ID (required)
- __-key__: The workspace API key (required)
- __-secret__: The workspace API secret (required)
- __-url__: The Structurizr API URL (optional; defaults to ```https://api.structurizr.com```)

## Example

To pull the content of a Structurizr workspace:

```
./structurizr.sh pull -id 123456 -key 1a130d2b... -secret a9daaf3e...
```