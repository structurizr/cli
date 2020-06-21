package com.structurizr.cli;

import com.structurizr.Workspace;
import com.structurizr.view.Styles;

abstract class AbstractCommand {

    private String version;

    AbstractCommand(String version) {
        this.version = version;
    }

    String getAgent() {
        return "structurizr-cli/" + version;

    }

    void addDefaultViewsAndStyles(Workspace workspace) {
        if (workspace.getViews().isEmpty()) {
            System.out.println(" - no views defined; creating default views");
            workspace.getViews().createDefaultViews();
        }

        Styles styles = workspace.getViews().getConfiguration().getStyles();
        if (styles.getElements().isEmpty() && styles.getRelationships().isEmpty() && workspace.getViews().getConfiguration().getThemes() == null) {
            System.out.println(" - no styles or themes defined; creating default styles");
            styles.addDefaultStyles();
        }
    }

}