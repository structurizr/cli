# lock

The ```lock``` command allows you to lock a Structurizr workspace (the cloud service or an on-premises installation).

## Options

- __-id__: The workspace ID (required)
- __-key__: The workspace API key (required)
- __-secret__: The workspace API secret (required)
- __-url__: The Structurizr API URL (optional; defaults to ```https://api.structurizr.com```)

## Example

To lock a Structurizr workspace:

```
java -jar structurizr-cli-*.jar lock -id 40120 -key 1a130d2b... -secret a9daaf3e...```