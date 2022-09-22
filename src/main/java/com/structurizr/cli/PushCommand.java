package com.structurizr.cli;

import com.structurizr.Workspace;
import com.structurizr.api.StructurizrClient;
import com.structurizr.dsl.StructurizrDslParser;
import com.structurizr.encryption.AesEncryptionStrategy;
import com.structurizr.util.StringUtils;
import com.structurizr.util.WorkspaceUtils;
import org.apache.commons.cli.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;

class PushCommand extends AbstractCommand {

    private static final Log log = LogFactory.getLog(PushCommand.class);

    PushCommand() {
    }

    public void run(String... args) throws Exception {
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

        option = new Option("w", "workspace", true, "Path to Structurizr JSON file/DSL file(s)");
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
            passphrase = cmd.getOptionValue("passphrase");
            mergeFromRemote = Boolean.parseBoolean(cmd.getOptionValue("merge", "true"));
            archive = Boolean.parseBoolean(cmd.getOptionValue("archive", "true"));

            if (StringUtils.isNullOrEmpty(workspacePath)) {
                log.error("-workspace must be specified");
                formatter.printHelp("push", options);
                System.exit(1);
            }
        } catch (ParseException e) {
            log.error(e.getMessage());
            formatter.printHelp("push", options);
            System.exit(1);
        }

        log.info("Pushing workspace " + workspaceId + " to " + apiUrl);

        StructurizrClient structurizrClient = new StructurizrClient(apiUrl, apiKey, apiSecret);
        structurizrClient.setAgent(getAgent());
        structurizrClient.setWorkspaceArchiveLocation(null);

        if (!StringUtils.isNullOrEmpty(passphrase)) {
            log.info(" - using client-side encryption");
            structurizrClient.setEncryptionStrategy(new AesEncryptionStrategy(passphrase));
        }

        Workspace workspace;
        File archivePath = new File(".");

        File path = new File(workspacePath);
        archivePath = path.getParentFile();
        if (!path.exists()) {
            log.error(" - workspace path " + workspacePath + " does not exist");
            System.exit(1);
        }

        log.info(" - creating new workspace");
        log.info(" - parsing model and views from " + path.getCanonicalPath());

        if (workspacePath.endsWith(".json")) {
            workspace = WorkspaceUtils.loadWorkspaceFromJson(path);
            workspace.setRevision(null);
        } else {
            StructurizrDslParser structurizrDslParser = new StructurizrDslParser();
            structurizrDslParser.parse(path);

            workspace = structurizrDslParser.getWorkspace();
        }

        log.info(" - merge layout from remote: " + mergeFromRemote);
        structurizrClient.setMergeFromRemote(mergeFromRemote);

        addDefaultViewsAndStyles(workspace);

        if (archive) {
            structurizrClient.setWorkspaceArchiveLocation(archivePath);
            log.info(" - storing previous version of workspace in " + structurizrClient.getWorkspaceArchiveLocation());
        }

        log.info(" - pushing workspace");
        structurizrClient.putWorkspace(workspaceId, workspace);

        log.info(" - finished");
    }

}