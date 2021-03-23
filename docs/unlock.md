# unlock

The ```unlock``` command allows you to unlock a Structurizr workspace (the cloud service or an on-premises installation).

## Options

- __-id__: The workspace ID (required)
- __-key__: The workspace API key (required)
- __-secret__: The workspace API secret (required)
- __-url__: The Structurizr API URL (optional; defaults to ```https://api.structurizr.com```)

## Example

To unlock a Structurizr workspace:

```
./structurizr.sh unlock -id 123456 -key 1a130d2b... -secret a9daaf3e...
```