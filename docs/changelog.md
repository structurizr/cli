# Changelog

## 1.7.0 (unreleased)

- Removes the dynamic view restrictions related to adding containers/components outside the scoped software system/container
- Enhanced the rules relating to whether elements can be added to a view or not
- Enhanced the logic to merge layout information of elements on views

## 1.6.0 (30th November 2020)

- Adds support for [implicit relationship source elements](https://github.com/structurizr/dsl/blob/master/docs/language-reference.md#relationship) (defining relationships inside an element block)
- Adds support for defining/changing the [terminology used when rendering diagrams](https://github.com/structurizr/dsl/blob/master/docs/language-reference.md#terminology)
- Adds support for including/excluding relationships on views using an expression of the form: [include/exclude <*|identifier> -> <*|identifier>](https://github.com/structurizr/dsl/blob/master/docs/language-reference.md#including-relationships)
- Fixes an issue with hyperlinks being generated incorrectly for ADRs associated with an element

## 1.5.1 (23rd November 2020)

- Bug fixes (#13, #15, #16)

## 1.5.0 (12th November 2020)

 - Importing documentation and associating it with the workspace or a software system - [`!docs`](https://github.com/structurizr/dsl/blob/master/docs/language-reference.md#documentation)
- Importing ADRs and associating them with the workspace or a software system - [`!adrs`](https://github.com/structurizr/dsl/blob/master/docs/language-reference.md#architecture-decision-records-adrs)
 - A title property on views - [`title`](https://github.com/structurizr/dsl/blob/master/docs/language-reference.md#title)
 - Adding perspectives to elements/relationships - [`perspectives`](https://github.com/structurizr/dsl/blob/master/docs/language-reference.md#perspectives)
 - Preventing implied relationships from being created - [`impliedRelationships`](https://github.com/structurizr/dsl/blob/master/docs/language-reference.md#impliedrelationships)