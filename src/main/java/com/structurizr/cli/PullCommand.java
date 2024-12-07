package com.structurizr.cli;

import com.structurizr.Workspace;
import com.structurizr.api.WorkspaceApiClient;
import com.structurizr.encryption.AesEncryptionStrategy;
import com.structurizr.util.StringUtils;
import com.structurizr.util.WorkspaceUtils;
import org.apache.commons.cli.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;

class PullCommand extends AbstractCommand {

    private static final Log log = LogFactory.getLog(PullCommand.class);

    PullCommand() {
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

        option = new Option("passphrase", "passphrase", true, "Client-side encryption passphrase");
        option.setRequired(false);
        options.addOption(option);

        CommandLineParser commandLineParser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        String apiUrl = "";
        long workspaceId = 1;
        String apiKey = "";
        String apiSecret = "";
        String branch = "";
        String passphrase = "";

        try {
            CommandLine cmd = commandLineParser.parse(options, args);

            apiUrl = cmd.getOptionValue("structurizrApiUrl", "https://api.structurizr.com");
            workspaceId = Long.parseLong(cmd.getOptionValue("workspaceId"));
            apiKey = cmd.getOptionValue("apiKey");
            apiSecret = cmd.getOptionValue("apiSecret");
            branch = cmd.getOptionValue("branch");
            passphrase = cmd.getOptionValue("passphrase");
        } catch (ParseException e) {
            log.error(e.getMessage());
            formatter.printHelp("pull", options);

            System.exit(1);
        }

        File file;
        if (StringUtils.isNullOrEmpty(branch)) {
            log.info("Pulling workspace " + workspaceId + " from " + apiUrl);
            file = new File("structurizr-" + workspaceId + "-workspace.json");
        } else {
            log.info("Pulling workspace " + workspaceId + " from " + apiUrl + " (branch=" + branch + ")");
            file = new File("structurizr-" + workspaceId + "-" + branch + "-workspace.json");
        }

        WorkspaceApiClient client = new WorkspaceApiClient(apiUrl, apiKey, apiSecret);
        client.setBranch(branch);
        client.setAgent(getAgent());

        if (!StringUtils.isNullOrEmpty(passphrase)) {
            log.info(" - using client-side encryption");
            client.setEncryptionStrategy(new AesEncryptionStrategy(passphrase));
        }

        Workspace workspace = client.getWorkspace(workspaceId);

        WorkspaceUtils.saveWorkspaceToJson(workspace, file);
        log.info(" - workspace saved as " + file.getCanonicalPath());
        log.info(" - finished");
    }

}