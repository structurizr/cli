package com.structurizr.cli.export;

import com.structurizr.export.WorkspaceExport;

public class JsonWorkspaceExport extends WorkspaceExport {

    public JsonWorkspaceExport(String definition) {
        super(definition);
    }

    @Override
    public String getFileExtension() {
        return "json";
    }

}
