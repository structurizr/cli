package com.structurizr.cli;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.structurizr.view.ElementStyle;
import com.structurizr.view.RelationshipStyle;

import java.util.Collection;
import java.util.LinkedList;

class Theme {

    private String name;
    private String description;
    private Collection<ElementStyle> elements = new LinkedList<>();
    private Collection<RelationshipStyle> relationships = new LinkedList<>();

    Theme() {
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    Collection<ElementStyle> getElements() {
        return elements;
    }

    void setElements(Collection<ElementStyle> elements) {
        this.elements = elements;
    }

    Collection<RelationshipStyle> getRelationships() {
        return relationships;
    }

    void setRelationships(Collection<RelationshipStyle> relationships) {
        this.relationships = relationships;
    }

}