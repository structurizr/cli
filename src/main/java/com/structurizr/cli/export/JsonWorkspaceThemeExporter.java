package com.structurizr.cli.export;

import com.structurizr.Workspace;
import com.structurizr.export.WorkspaceExport;
import com.structurizr.export.WorkspaceExporter;
import com.structurizr.view.ThemeUtils;

public class JsonWorkspaceThemeExporter implements WorkspaceExporter {

    @Override
    public WorkspaceExport export(Workspace workspace) {
        try {
            return new JsonWorkspaceExport(ThemeUtils.toJson(workspace));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
