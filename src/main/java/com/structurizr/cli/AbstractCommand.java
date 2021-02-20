package com.structurizr.cli;

import com.structurizr.Workspace;
import com.structurizr.dsl.StructurizrDslParser;
import com.structurizr.util.WorkspaceUtils;
import com.structurizr.view.Styles;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.File;

abstract class AbstractCommand {

    private static final int HTTP_OK_STATUS = 200;

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

    protected Workspace loadWorkspace(String workspacePathAsString) throws Exception {
        Workspace workspace;

        if (workspacePathAsString.endsWith(".json")) {
            if (workspacePathAsString.startsWith("http://") || workspacePathAsString.startsWith("https")) {
                String json = readFromUrl(workspacePathAsString);
                workspace = WorkspaceUtils.fromJson(json);
            } else {
                File workspacePath = new File(workspacePathAsString);
                workspace = WorkspaceUtils.loadWorkspaceFromJson(workspacePath);
            }

        } else {
            StructurizrDslParser structurizrDslParser = new StructurizrDslParser();

            if (workspacePathAsString.startsWith("http://") || workspacePathAsString.startsWith("https")) {
                String dsl = readFromUrl(workspacePathAsString);
                structurizrDslParser.parse(dsl);
            } else {
                File workspacePath = new File(workspacePathAsString);
                structurizrDslParser.parse(workspacePath);
            }

            workspace = structurizrDslParser.getWorkspace();
        }

        return workspace;
    }

    protected String readFromUrl(String url) {
        try (CloseableHttpClient httpClient = HttpClients.createSystem()) {
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpResponse response = httpClient.execute(httpGet);

            if (response.getCode() == HTTP_OK_STATUS) {
                return EntityUtils.toString(response.getEntity());
            }
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }

        return "";
    }

}