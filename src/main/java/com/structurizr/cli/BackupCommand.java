package com.structurizr.cli;

import com.structurizr.api.AdminApiClient;
import com.structurizr.api.WorkspaceApiClient;
import com.structurizr.api.WorkspaceMetadata;
import com.structurizr.util.StringUtils;
import org.apache.commons.cli.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.List;
import java.util.Properties;

class BackupCommand extends AbstractCommand {

    private static final Log log = LogFactory.getLog(BackupCommand.class);

    BackupCommand() {
    }

    public void run(String... args) throws Exception {
        Options options = new Options();

        Option option = new Option("url", "structurizrApiUrl", true, "Structurizr API URL (default: https://api.structurizr.com)");
        option.setRequired(false);
        options.addOption(option);

        option = new Option("key", "apiKey", true, "API key");
        option.setRequired(true);
        options.addOption(option);

        option = new Option("user", "username", true, "Username (only required for cloud service)");
        option.setRequired(false);
        options.addOption(option);

        option = new Option("o", "output", true, "Path to an output directory");
        option.setRequired(false);
        options.addOption(option);

        option = new Option("debug", "debug", false, "Enable debug logging");
        option.setRequired(false);
        options.addOption(option);

        CommandLineParser commandLineParser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        String apiUrl = "";
        String username = "";
        String apiKey = "";
        boolean debug = false;
        String outputPath = null;

        try {
            CommandLine cmd = commandLineParser.parse(options, args);

            apiUrl = cmd.getOptionValue("structurizrApiUrl", "https://api.structurizr.com");
            apiKey = cmd.getOptionValue("apiKey");
            username = cmd.getOptionValue("username");
            outputPath = cmd.getOptionValue("output");
            debug = cmd.hasOption("debug");
        } catch (ParseException e) {
            log.error(e.getMessage());
            formatter.printHelp("backup", options);

            System.exit(1);
        }

        if (debug) {
            configureDebugLogging();
        }

        File outputDir = new File(outputPath);
        if (outputDir.exists()) {
            throw new RuntimeException("Output directory already exists: " + outputDir.getAbsolutePath());
        }

        AdminApiClient adminApiClient = new AdminApiClient(apiUrl, username, apiKey);
        List<WorkspaceMetadata> workspaces = adminApiClient.getWorkspaces();

        if (!workspaces.isEmpty()) {
            outputDir.mkdirs();

            File structurizrPropertiesFile = new File(outputDir, "structurizr.properties");
            Files.writeString(structurizrPropertiesFile.toPath(), "structurizr.workspaces=*");
            log.info(" - " + structurizrPropertiesFile.getCanonicalPath());

            for (WorkspaceMetadata workspaceMetadata : workspaces) {
                File workspaceDirectory = new File(outputDir, "" + workspaceMetadata.getId());
                workspaceDirectory.mkdirs();

                WorkspaceApiClient client = new WorkspaceApiClient(apiUrl, workspaceMetadata.getApiKey(), workspaceMetadata.getApiSecret());
                client.setAgent(getAgent());

                String json = client.getWorkspaceAsJson(workspaceMetadata.getId());
                File workspaceJsonFile = new File(workspaceDirectory, "workspace.json");
                Files.writeString(workspaceJsonFile.toPath(), json);
                log.info(" - " + workspaceJsonFile.getCanonicalPath());

                Properties properties = new Properties();
                properties.setProperty("name", workspaceMetadata.getName() != null ? workspaceMetadata.getName() : "");
                properties.setProperty("description", workspaceMetadata.getDescription() != null ? workspaceMetadata.getDescription() : "");
                properties.setProperty("apiKey", workspaceMetadata.getApiKey());
                properties.setProperty("apiSecret", workspaceMetadata.getApiSecret());
                properties.setProperty("public", "" + !StringUtils.isNullOrEmpty(workspaceMetadata.getPublicUrl()));
                if (!StringUtils.isNullOrEmpty(workspaceMetadata.getShareableUrl())) {
                    properties.setProperty("sharingToken", workspaceMetadata.getShareableUrl().substring(workspaceMetadata.getShareableUrl().lastIndexOf("/") + 1));
                }
                properties.setProperty("clientSideEncrypted", "" + (json.contains("\"encryptionStrategy\"") && json.contains("\"ciphertext\"")));

                File workspacePropertiesFile = new File(workspaceDirectory, "workspace.properties");
                FileWriter fileWriter = new FileWriter(workspacePropertiesFile);
                properties.store(fileWriter, null);
                fileWriter.flush();
                fileWriter.close();
                log.info(" - " + workspacePropertiesFile.getCanonicalPath());
            }
        }
    }

}