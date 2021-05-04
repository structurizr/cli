package com.structurizr.cli;

import com.structurizr.Workspace;
import com.structurizr.api.StructurizrClient;
import com.structurizr.documentation.AdrToolsImporter;
import com.structurizr.documentation.AutomaticDocumentationTemplate;
import com.structurizr.dsl.StructurizrDslParser;
import com.structurizr.encryption.AesEncryptionStrategy;
import com.structurizr.util.StringUtils;
import com.structurizr.util.WorkspaceUtils;
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

        option = new Option("workspace", "workspace", true, "Path to Structurizr JSON file/DSL file(s)");
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

        option = new Option("merge", "mergeFromRemote", true, "Whether to merge layout information from the remote workspace");
        option.setRequired(false);
        options.addOption(option);

        option = new Option("archive", "archive", true, "Stores the previous version of the remote workspace");
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
        boolean mergeFromRemote = true;
        boolean archive = true;

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
            mergeFromRemote = Boolean.parseBoolean(cmd.getOptionValue("merge", "true"));
            archive = Boolean.parseBoolean(cmd.getOptionValue("archive", "true"));

            if (StringUtils.isNullOrEmpty(workspacePath) && StringUtils.isNullOrEmpty(documentationPath) && StringUtils.isNullOrEmpty(decisionsPath)) {
                System.out.println("One of -workspace, -docs, or -adrs must be specified");
                formatter.printHelp("push", options);
                System.exit(1);
            }
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
        File archivePath = new File(".");

        if (!StringUtils.isNullOrEmpty(workspacePath)) {
            File path = new File(workspacePath);
            archivePath = path.getParentFile();
            if (!path.exists()) {
                System.out.println(" - workspace path " + workspacePath + " does not exist");
                System.exit(1);
            }

            System.out.println(" - creating new workspace");
            System.out.println(" - parsing model and views from " + path.getCanonicalPath());

            if (workspacePath.endsWith(".json")) {
                workspace = WorkspaceUtils.loadWorkspaceFromJson(path);
                workspace.setRevision(null);
            } else {
                StructurizrDslParser structurizrDslParser = new StructurizrDslParser();
                structurizrDslParser.parse(path);

                workspace = structurizrDslParser.getWorkspace();
            }

            System.out.println(" - merge layout from remote: " + mergeFromRemote);
            structurizrClient.setMergeFromRemote(mergeFromRemote);

            addDefaultViewsAndStyles(workspace);
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
        template.setRecursive(true);

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

        if (archive) {
            structurizrClient.setWorkspaceArchiveLocation(archivePath);
            System.out.println(" - storing previous version of workspace in " + structurizrClient.getWorkspaceArchiveLocation());
        }

        System.out.println(" - pushing workspace");
        structurizrClient.putWorkspace(workspaceId, workspace);

        System.out.println(" - finished");
    }

}