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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

class CloudToOnPremisesCommand extends AbstractCommand {

    private static final Log log = LogFactory.getLog(CloudToOnPremisesCommand.class);
    private static final String CLOUD_SERVICE_API_URL = "https://api.structurizr.com";

    CloudToOnPremisesCommand() {
    }

    public void run(String... args) throws Exception {
        Options options = new Options();

        Option option = new Option("key", "apiKey", true, "API key");
        option.setRequired(true);
        options.addOption(option);

        option = new Option("user", "username", true, "Username");
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

        String username = "";
        String apiKey = "";
        boolean debug = false;
        String outputPath = null;

        try {
            CommandLine cmd = commandLineParser.parse(options, args);

            apiKey = cmd.getOptionValue("apiKey");
            username = cmd.getOptionValue("username");
            outputPath = cmd.getOptionValue("output");
            debug = cmd.hasOption("debug");
        } catch (ParseException e) {
            log.error(e.getMessage());
            formatter.printHelp("cloud-to-onpremises", options);

            System.exit(1);
        }

        if (debug) {
            configureDebugLogging();
        }

        File outputDir = new File(outputPath);
        if (outputDir.exists()) {
            throw new RuntimeException("Output directory already exists: " + outputDir.getAbsolutePath());
        }

        AdminApiClient adminApiClient = new AdminApiClient(CLOUD_SERVICE_API_URL, username, apiKey);
        List<WorkspaceMetadata> workspaces = adminApiClient.getWorkspaces();

        if (!workspaces.isEmpty()) {
            outputDir.mkdirs();

            File structurizrPropertiesFile = new File(outputDir, "structurizr.properties");
            Files.writeString(structurizrPropertiesFile.toPath(), "structurizr.feature.workspace.branches=true");
            log.info(" - " + structurizrPropertiesFile.getCanonicalPath());

            File structurizrUsersFile = new File(outputDir, "structurizr.users");
            Files.writeString(structurizrUsersFile.toPath(), username + "=$2a$06$uM5wM.eJwrPq1RM/gBXRr.d0bfyu9ABxdE56qYbRLSCZzqfR7xHcC");
            log.info(" - " + structurizrUsersFile.getCanonicalPath());

            for (WorkspaceMetadata workspaceMetadata : workspaces) {
                File workspaceDirectory = new File(outputDir, "" + workspaceMetadata.getId());
                workspaceDirectory.mkdirs();

                WorkspaceApiClient client = new WorkspaceApiClient(CLOUD_SERVICE_API_URL, workspaceMetadata.getApiKey(), workspaceMetadata.getApiSecret());
                client.setAgent(getAgent());
                client.setWorkspaceArchiveLocation(null);

                // main branch
                String json = client.getWorkspaceAsJson(workspaceMetadata.getId());
                File workspaceJsonFile = new File(workspaceDirectory, "workspace.json");
                Files.writeString(workspaceJsonFile.toPath(), json);
                log.info(" - " + workspaceJsonFile.getCanonicalPath());

                // branches
                if (workspaceMetadata.getBranches() != null) {
                    File branchesDirectory = new File(workspaceDirectory, "branches");
                    branchesDirectory.mkdirs();

                    for (String branch : workspaceMetadata.getBranches()) {
                        File branchDirectory = new File(branchesDirectory, branch);
                        branchDirectory.mkdir();

                        client.setBranch(branch);
                        json = client.getWorkspaceAsJson(workspaceMetadata.getId());
                        File branchJsonFile = new File(branchDirectory, "workspace.json");
                        Files.writeString(branchJsonFile.toPath(), json);
                        log.info(" - " + branchJsonFile.getCanonicalPath());
                    }
                }

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

                properties.setProperty("owner", workspaceMetadata.getUsers().getOwner());

                List<String> writeUsers = new ArrayList<>();
                writeUsers.add(workspaceMetadata.getUsers().getOwner());
                Collections.addAll(writeUsers, workspaceMetadata.getUsers().getAdmin());
                Collections.addAll(writeUsers, workspaceMetadata.getUsers().getWrite());

                properties.setProperty("writeUsers", String.join(",", writeUsers.toArray(new String[0])));
                properties.setProperty("readUsers", String.join(",", workspaceMetadata.getUsers().getWrite()));

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