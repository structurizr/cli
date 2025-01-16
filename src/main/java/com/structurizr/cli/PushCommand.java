package com.structurizr.cli;

import com.structurizr.Workspace;
import com.structurizr.api.WorkspaceApiClient;
import com.structurizr.encryption.AesEncryptionStrategy;
import com.structurizr.util.StringUtils;
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

        Option option = new Option("url", "structurizrApiUrl", true, "Structurizr API URL (default: https://api.structurizr.com)");
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

        option = new Option("branch", "branch", true, "Branch name");
        option.setRequired(false);
        options.addOption(option);

        option = new Option("w", "workspace", true, "Path or URL to the workspace JSON/DSL file");
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

        option = new Option("debug", "debug", false, "Enable debug logging");
        option.setRequired(false);
        options.addOption(option);

        CommandLineParser commandLineParser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        String apiUrl = "";
        long workspaceId = 1;
        String apiKey = "";
        String apiSecret = "";
        String branch = "";
        String workspacePath = "";
        String passphrase = "";
        boolean mergeFromRemote = true;
        boolean archive = true;
        boolean debug = false;

        try {
            CommandLine cmd = commandLineParser.parse(options, args);

            apiUrl = cmd.getOptionValue("structurizrApiUrl", "https://api.structurizr.com");
            workspaceId = Long.parseLong(cmd.getOptionValue("workspaceId"));
            apiKey = cmd.getOptionValue("apiKey");
            apiSecret = cmd.getOptionValue("apiSecret");
            branch = cmd.getOptionValue("branch");
            workspacePath = cmd.getOptionValue("workspace");
            passphrase = cmd.getOptionValue("passphrase");
            mergeFromRemote = Boolean.parseBoolean(cmd.getOptionValue("merge", "true"));
            archive = Boolean.parseBoolean(cmd.getOptionValue("archive", "true"));
            debug = cmd.hasOption("debug");

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

        if (debug) {
            configureDebugLogging();
        }

        if (StringUtils.isNullOrEmpty(branch)) {
            log.info("Pushing workspace " + workspaceId + " to " + apiUrl);
        } else {
            log.info("Pushing workspace " + workspaceId + " to " + apiUrl + " (branch=" + branch + ")");
        }

        WorkspaceApiClient client = new WorkspaceApiClient(apiUrl, apiKey, apiSecret);
        client.setBranch(branch);
        client.setAgent(getAgent());
        client.setWorkspaceArchiveLocation(null);

        if (!StringUtils.isNullOrEmpty(passphrase)) {
            log.info(" - using client-side encryption");
            client.setEncryptionStrategy(new AesEncryptionStrategy(passphrase));
        }

        File archivePath = new File(".");

        File path = new File(workspacePath);
        archivePath = path.getParentFile();
        if (!path.exists()) {
            log.error(" - workspace path " + workspacePath + " does not exist");
            System.exit(1);
        }

        log.info(" - creating new workspace");
        log.info(" - parsing model and views from " + path.getCanonicalPath());

        Workspace workspace = loadWorkspace(workspacePath);

        log.info(" - merge layout from remote: " + mergeFromRemote);
        client.setMergeFromRemote(mergeFromRemote);

        addDefaultViewsAndStyles(workspace);

        if (archive) {
            client.setWorkspaceArchiveLocation(archivePath);
            log.info(" - storing previous version of workspace in " + client.getWorkspaceArchiveLocation());
        }

        log.info(" - pushing workspace");
        client.putWorkspace(workspaceId, workspace);

        log.info(" - finished");
    }

}