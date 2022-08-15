package com.structurizr.cli.export;

import com.structurizr.Workspace;
import com.structurizr.dsl.DslUtils;
import com.structurizr.dsl.StructurizrDslFormatter;
import com.structurizr.export.WorkspaceExport;
import com.structurizr.export.WorkspaceExporter;
import com.structurizr.util.StringUtils;
import com.structurizr.util.WorkspaceUtils;

public class DslWorkspaceExporter implements WorkspaceExporter {

    @Override
    public WorkspaceExport export(Workspace workspace) {
        try {
            String dsl = DslUtils.getDsl(workspace);

            if (StringUtils.isNullOrEmpty(dsl)) {
                StructurizrDslFormatter structurizrDslFormatter = new StructurizrDslFormatter();
                dsl = structurizrDslFormatter.format(WorkspaceUtils.toJson(workspace, false));
            }

            return new DslWorkspaceExport(dsl);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
