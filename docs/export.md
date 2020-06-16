# export

The ```export``` command allows you to export the views within a Structurizr workspace to a number of different formats. Files will be created in the current directory, one per view that has been exported.

## Options

- __-workspace__: The path to the workspace JSON/DSL file (required)
- __-format__: plantuml|websequencediagrams (required)

## Examples

To export all diagrams in a JSON workspace to PlantUML format::

```
java -jar structurizr-cli-*.jar export -workspace myworkspace.json -format plantuml
```

To export all diagrams in a DSL workspace to WebSequenceDiagrams format::

```
java -jar structurizr-cli-*.jar export -workspace myworkspace.dsl -format websequencediagrams
```