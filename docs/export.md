# export

The ```export``` command allows you to export the views within a Structurizr workspace to a number of different formats.
Files will be created one per view that has been exported.
If output directory is not specified, files will be created in the same directory as the workspace.

## Options

- __-workspace__: The path or URL to the workspace JSON file/DSL file(s) (required)
- __-format__: plantuml|mermaid|websequencediagrams|ilograph|json (required)
- __-output__: Relative or absolute path to an output directory (optional)

## Examples

To export all views in a JSON workspace to PlantUML format under folder named 'diagrams':

```
java -jar structurizr-cli-*.jar export -workspace myworkspace.json -format plantuml -output diagrams
```

To export all views in a JSON workspace to Mermaid format:

```
java -jar structurizr-cli-*.jar export -workspace myworkspace.json -format mermaid
```

To export all dynamic views in a DSL workspace to WebSequenceDiagrams format:

```
java -jar structurizr-cli-*.jar export -workspace myworkspace.dsl -format websequencediagrams
```

To export a DSL workspace to the JSON workspace format:

```
java -jar structurizr-cli-*.jar export -workspace myworkspace.dsl -format json
```

