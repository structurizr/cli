package com.structurizr.cli;

import com.structurizr.Workspace;
import com.structurizr.api.StructurizrClient;
import com.structurizr.documentation.AdrToolsImporter;
import com.structurizr.documentation.AutomaticDocumentationTemplate;
import com.structurizr.dsl.StructurizrDslParser;
import com.structurizr.encryption.AesEncryptionStrategy;
import com.structurizr.model.Container;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.util.StringUtils;
import com.structurizr.view.*;
import org.apache.commons.cli.*;

import java.io.File;

class PushCommand extends AbstractCommand {

    PushCommand(String version) {
        super(version);
    }

    void run(String... args) throws Exception {
        Options options = new Options();

        Option option = new Option("url", "structurizrApiUrl", true, "Structurizr API URL (default: https://api.structurizr.com");
        option.setRequired(false);
        options.addOption(option);

        option = new Option("id", "workspaceId", true, "Workspace ID");
        option.setRequired(true);
        options.addOption(option);

        option = new Option("key", "apiKey", true, "Workspace API key");
        option.setRequired(true);
        options.addOption(option);

        option = new Option("secret", "apiSecret", true, "Workspace API secret");
        option.setRequired(true);
        options.addOption(option);

        option = new Option("workspace", "workspace", true, "Path to Structurizr DSL file(s)");
        option.setRequired(false);
        options.addOption(option);

        option = new Option("docs", "documentation", true, "Path to documentation");
        option.setRequired(false);
        options.addOption(option);

        option = new Option("adrs", "decisions", true, "Path to decisions");
        option.setRequired(false);
        options.addOption(option);

        option = new Option("passphrase", "passphrase", true, "Client-side encryption passphrase");
        option.setRequired(false);
        options.addOption(option);

        CommandLineParser commandLineParser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        String apiUrl = "";
        long workspaceId = 1;
        String apiKey = "";
        String apiSecret = "";
        String workspacePath = "";
        String documentationPath = "";
        String decisionsPath = "";
        String passphrase = "";

        try {
            CommandLine cmd = commandLineParser.parse(options, args);

            apiUrl = cmd.getOptionValue("structurizrApiUrl", "https://api.structurizr.com");
            workspaceId = Long.parseLong(cmd.getOptionValue("workspaceId"));
            apiKey = cmd.getOptionValue("apiKey");
            apiSecret = cmd.getOptionValue("apiSecret");
            workspacePath = cmd.getOptionValue("workspace");
            documentationPath = cmd.getOptionValue("docs");
            decisionsPath = cmd.getOptionValue("adrs");
            passphrase = cmd.getOptionValue("passphrase");
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("push", options);

            System.exit(1);
        }

        System.out.println("Pushing workspace " + workspaceId + " to " + apiUrl);

        StructurizrClient structurizrClient = new StructurizrClient(apiUrl, apiKey, apiSecret);
        structurizrClient.setAgent(getAgent());
        structurizrClient.setWorkspaceArchiveLocation(null);

        if (!StringUtils.isNullOrEmpty(passphrase)) {
            System.out.println(" - using client-side encryption");
            structurizrClient.setEncryptionStrategy(new AesEncryptionStrategy(passphrase));
        }

        Workspace workspace;

        if (!StringUtils.isNullOrEmpty(workspacePath)) {
            File path = new File(workspacePath);
            if (!path.exists()) {
                System.out.println(" - workspace path " + workspacePath + " does not exist");
                System.exit(1);
            }

            System.out.println(" - creating new workspace");
            System.out.println(" - parsing model and views from " + path.getCanonicalPath());
            StructurizrDslParser structurizrDslParser = new StructurizrDslParser();
            structurizrDslParser.parse(path);

            workspace = structurizrDslParser.getWorkspace();
            structurizrClient.setMergeFromRemote(true);

            if (workspace.getViews().isEmpty()) {
                System.out.println(" - no views defined; creating default views");
                createDefaultViews(workspace);
            }
        } else {
            System.out.println(" - pulling existing workspace " + workspaceId + " from " + apiUrl);
            workspace = structurizrClient.getWorkspace(workspaceId);
            workspace.setRevision(null);
            structurizrClient.setMergeFromRemote(false);

            if (!StringUtils.isNullOrEmpty(documentationPath) || !StringUtils.isNullOrEmpty(decisionsPath)) {
                System.out.println(" - clearing documentation and decisions in workspace");
                workspace.getDocumentation().clear();
            }
        }

        AutomaticDocumentationTemplate template = new AutomaticDocumentationTemplate(workspace);

        if (!StringUtils.isNullOrEmpty(documentationPath)) {
            File path = new File(documentationPath);
            if (!path.exists()) {
                System.out.println(" - documentation path " + documentationPath + " does not exist");
                System.exit(1);
            }

            if (!path.isDirectory()) {
                System.out.println(" - documentation path " + documentationPath + " is not a directory");
                System.exit(1);
            }

            System.out.println(" - importing documentation from " + path.getCanonicalPath());
            template.addSections(path);
            template.addImages(path);
        }

        if (!StringUtils.isNullOrEmpty(decisionsPath)) {
            File path = new File(decisionsPath);
            if (!path.exists()) {
                System.out.println(" - decisions path " + decisionsPath + " does not exist");
                System.exit(1);
            }

            if (!path.isDirectory()) {
                System.out.println(" - decisions path " + decisionsPath + " is not a directory");
                System.exit(1);
            }

            System.out.println(" - importing ADRs from " + path.getCanonicalPath());
            AdrToolsImporter adrToolsImporter = new AdrToolsImporter(workspace, path);
            adrToolsImporter.importArchitectureDecisionRecords();
        }

        System.out.println(" - pushing workspace");
        structurizrClient.putWorkspace(workspaceId, workspace);

        System.out.println(" - finished");
    }

    private void createDefaultViews(Workspace workspace) {
        // create a single System Landscape diagram containing all people and software systems
        SystemLandscapeView systemLandscapeView = workspace.getViews().createSystemLandscapeView("SystemLandscape", "A system landscape view for " + workspace.getName());
        systemLandscapeView.addAllElements();
        systemLandscapeView.enableAutomaticLayout(AutomaticLayout.RankDirection.TopBottom, 300, 300);

        // and a system context view plus container view for each software system
        for (SoftwareSystem softwareSystem : workspace.getModel().getSoftwareSystems()) {
            SystemContextView systemContextView = workspace.getViews().createSystemContextView(softwareSystem, "SystemContext-" + encode(softwareSystem.getName()), "A system context view for " + softwareSystem.getName() + ".");
            systemContextView.addNearestNeighbours(softwareSystem);
            systemContextView.enableAutomaticLayout(AutomaticLayout.RankDirection.TopBottom, 300, 300);

            if (softwareSystem.getContainers().size() > 0) {
                ContainerView containerView = workspace.getViews().createContainerView(softwareSystem, "Containers-" + encode(softwareSystem.getName()), "A container view for " + softwareSystem.getName() + ".");
                softwareSystem.getContainers().forEach(containerView::addNearestNeighbours);
                containerView.enableAutomaticLayout(AutomaticLayout.RankDirection.TopBottom, 300, 300);

                for (Container container : softwareSystem.getContainers()) {
                    if (container.getComponents().size() > 0) {
                        ComponentView componentView = workspace.getViews().createComponentView(container, "Components-" + encode(container.getName()), "A component view for " + container.getName() + ".");
                        container.getComponents().forEach(componentView::addNearestNeighbours);
                        componentView.enableAutomaticLayout(AutomaticLayout.RankDirection.TopBottom, 300, 300);
                    }
                }
            }
        }
    }

    private String encode(String name) {
        return name.replaceAll("\\s", "");
    }

}