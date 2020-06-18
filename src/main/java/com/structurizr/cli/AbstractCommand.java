package com.structurizr.cli;

abstract class AbstractCommand {

    private String version;

    AbstractCommand(String version) {
        this.version = version;
    }

    protected String getAgent() {
        return "structurizr-cli/" + version;

    }

}