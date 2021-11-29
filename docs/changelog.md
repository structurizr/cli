# Changelog


- DSL: Adds the implied relationships functionality for custom elements.
- DSL: The "add default elements" feature (`include *`) will now also add any connected custom elements.
- DSL: Adds better support for custom elements when using element expressions.
- DSL: Adds a `description` keyword for setting the description on elements.
- DSL: Adds a `technology` keyword for setting the technology on containers, components, deployment nodes, and infrastructure nodes.
- Exports: Adds support for hyperlinked elements via the StructurizrPlantUML and C4-PlantUML exporters.
- Exports: Adds support for styling groups via an element style named `Group' (for all groups) or `Group:Name` (for the "Name" group).

## 1.15.0 (2nd October 2021)

- DSL: Adds support for specifying element style icons and the branding logo as a HTTPS/HTTP URL.
- DSL: Adds support for relationships from deployment nodes to infrastructure nodes.
- DSL: Fixes an issue where `this` didn't work when defining relationships inside deployment/infrastructure nodes.
- DSL: Removes the restriction that `include *` must be on a line of its own inside view definitions.

## 1.14.0 (19th September 2021)

- DSL: __Breaking change__: Adds support for software system/container instances in multiple deployment groups.
- DSL: Fixes an issue where internal software systems/people are not included by the DSL formatter when the enterprise is not set.
- DSL: The DSL formatter now removes empty tokens at the end of lines.
- DSL: Adds support for recognising escaped newlines (\n) in tokens.
- CLI: Adds support for C4-PlantUML `SHOW_LEGEND()`.
- CLI: Identifiers in PlantUML exports are now based upon element names, rather than internal IDs.

## 1.13.0 (3rd September 2021)

- CLI: Fixes issue #46 (CLI was ignoring `animation` flag when exporting to PlantUML).
- CLI: Distribution/packaging changed to better support DSL scripts and plugins.
- DSL: See changelog for [structurizr-dsl v1.13.0](https://github.com/structurizr/dsl/releases/tag/v1.13.0)

## 1.12.0 (30th June 2021)

- DSL: Adds an `!identifiers` keyword to specify whether element identifiers should be `flat` (default) or `hierarchical`.
- DSL: Adds support for a `this` identifier when defining relationships inside element definitions.
- DSL: Fixes links between ADRs.

## 1.11.0 (7th June 2021)

- CLI: __Breaking change__: Default styles are no longer added; use the `default` theme to add some default styles instead.
- CLI: Adds a `-archive` option to the [push](push.md) command to determine whether to store the previous version of the remote workspace.
- CLI: Adds a [list](list.md) command to list the elements within a workspace.
- CLI: Adds a `theme` format to the [export](export.md) command to export the theme from the specified workspace.
- CLI: Adds a `animation` option to the [export](export.md) command to export animation frames (PlantUML formats only).
- DSL: __Breaking change__: Relationship expressions (e.g. * -> *) now need to be surrounded in double quotes.
- DSL: Support parallel activities in dynamic view.
- DSL: Adds a `tags` keyword for adding tags to elements/relationships.
- DSL: Adds a `theme` keyword for adding a single theme.
- DSL: Adds support to `!include` from a HTTPS URL.
- DSL: Adds support for referencing groups by identifier.
- DSL: Adds support for extending a workspace.

## 1.10.1 (29th April 2021)

- Prevents the sequence diagram variant of dynamic views from being created when exporting as `plantuml/c4plantuml`, as C4-PlantUML doesn't "natively" support a sequence diagram (issue #39).

## 1.10.0 (27th April 2021)

- Adds support for a DOT export.
- Improvements to PlantUML and Mermaid and exporters (e.g. support for groups and "external" software system/container boundaries).
- Adds a 'Diamond' shape.

## 1.9.0 (20th March 2021)

- Adds support for exporting a JSON workspace to Structurizr DSL format.
- Adds support for adding individual infrastructure nodes, software system instances, and container instances to a deployment view.
- Adds support for removing software system instances from deployment views.
- Adds support for "deployment groups", providing a way to scope how software system/container instance relationships are replicated when added to deployment nodes.
- Adds support for including/excluding elements on static views based upon tags (https://github.com/structurizr/dsl/issues/35).
- Adds a `validate` command.
- Improved support for themes (e.g. when exporting to PlantUML), which now works the same as described at [Structurizr - Themes](https://structurizr.com/help/themes).

## 1.8.1 (4th February 2021)

- Bug fixes.

## 1.8.0 (28th January 2021)

- Adds support for locking and unlocking Structurizr workspaces.
- Adds support for other PlantUML writers (issue #23).
- Added a mergeFromRemote option to the push command, to control whether layout information is merged or not.
- Bug fixes.

## 1.7.0 (6th January 2021)

- Removes the dynamic view restrictions related to adding containers/components outside the scoped software system/container
- Enhanced the rules relating to whether elements can be added to a view or not
- Enhanced the logic to merge layout information of elements on views
- Resolves issue #19

## 1.6.0 (30th November 2020)

- Adds support for [implicit relationship source elements](https://github.com/structurizr/dsl/blob/master/docs/language-reference.md#relationship) (defining relationships inside an element block)
- Adds support for defining/changing the [terminology used when rendering diagrams](https://github.com/structurizr/dsl/blob/master/docs/language-reference.md#terminology)
- Adds support for including/excluding relationships on views using an expression of the form: [include/exclude <*|identifier> -> <*|identifier>](https://github.com/structurizr/dsl/blob/master/docs/language-reference.md#including-relationships)
- Fixes an issue with hyperlinks being generated incorrectly for ADRs associated with an element

## 1.5.1 (23rd November 2020)

- Resolves issues #13, #15, and #16

## 1.5.0 (12th November 2020)

- Importing documentation and associating it with the workspace or a software system - [`!docs`](https://github.com/structurizr/dsl/blob/master/docs/language-reference.md#documentation)
- Importing ADRs and associating them with the workspace or a software system - [`!adrs`](https://github.com/structurizr/dsl/blob/master/docs/language-reference.md#architecture-decision-records-adrs)
- A title property on views - [`title`](https://github.com/structurizr/dsl/blob/master/docs/language-reference.md#title)
- Adding perspectives to elements/relationships - [`perspectives`](https://github.com/structurizr/dsl/blob/master/docs/language-reference.md#perspectives)
- Preventing implied relationships from being created - [`impliedRelationships`](https://github.com/structurizr/dsl/blob/master/docs/language-reference.md#impliedrelationships)