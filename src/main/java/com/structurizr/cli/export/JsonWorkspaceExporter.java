package com.structurizr.cli.export;

import com.structurizr.Workspace;
import com.structurizr.export.WorkspaceExport;
import com.structurizr.export.WorkspaceExporter;
import com.structurizr.util.WorkspaceUtils;

public class JsonWorkspaceExporter implements WorkspaceExporter {

    @Override
    public WorkspaceExport export(Workspace workspace) {
        try {
            return new JsonWorkspaceExport(WorkspaceUtils.toJson(workspace, true));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
