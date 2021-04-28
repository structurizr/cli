# Changelog

## (unreleased)

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