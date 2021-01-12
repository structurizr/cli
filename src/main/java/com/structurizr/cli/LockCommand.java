package com.structurizr.cli;

import com.structurizr.api.StructurizrClient;
import org.apache.commons.cli.*;

class LockCommand extends AbstractCommand {

    LockCommand(String version) {
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

        CommandLineParser commandLineParser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        String apiUrl = "";
        long workspaceId = 1;
        String apiKey = "";
        String apiSecret = "";

        try {
            CommandLine cmd = commandLineParser.parse(options, args);

            apiUrl = cmd.getOptionValue("structurizrApiUrl", "https://api.structurizr.com");
            workspaceId = Long.parseLong(cmd.getOptionValue("workspaceId"));
            apiKey = cmd.getOptionValue("apiKey");
            apiSecret = cmd.getOptionValue("apiSecret");
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("pull", options);

            System.exit(1);
        }

        System.out.println("Locking workspace " + workspaceId + " at " + apiUrl);
        StructurizrClient structurizrClient = new StructurizrClient(apiUrl, apiKey, apiSecret);
        structurizrClient.setAgent(getAgent());
        boolean locked = structurizrClient.lockWorkspace(workspaceId);

        System.out.println(" - locked " + locked);
        System.out.println(" - finished");

        System.exit(locked ? 0 : 1);
    }

}