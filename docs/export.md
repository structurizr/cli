# export

The ```export``` command allows you to export the views within a Structurizr workspace to a number of different formats.
Files will be created one per view that has been exported.
If output directory is not specified, files will be created in the same directory as the workspace.

Please note that some export formats do not support all of the available shapes/features - see [Structurizr - Rendering tools](https://structurizr.org/#rendering) for a comparison.

## Options

- __-workspace__: The path or URL to the workspace JSON file/DSL file(s) (required)
- __-format__: plantuml|mermaid|websequencediagrams|dot|ilograph|json|dsl|theme (required)
- __-output__: Relative or absolute path to an output directory (optional)

The PlantUML export format can also be specified with a sub-format: `plantuml/structurizr` (default) or `plantuml/c4plantuml`.

## Examples

To export all views in a JSON workspace to PlantUML format under folder named 'diagrams':

```
./structurizr.sh export -workspace workspace.json -format plantuml -output diagrams
```

To export all views in a JSON workspace to PlantUML format, using C4-PlantUML, under folder named 'diagrams':

```
./structurizr.sh export -workspace workspace.json -format plantuml/c4plantuml -output diagrams
```

To export all views in a JSON workspace to Mermaid format:

```
./structurizr.sh export -workspace workspace.json -format mermaid
```

To export all dynamic views in a DSL workspace to WebSequenceDiagrams format:

```
./structurizr.sh export -workspace workspace.dsl -format websequencediagrams
```

To export a DSL workspace to the JSON workspace format:

```
./structurizr.sh export -workspace workspace.dsl -format json
```

