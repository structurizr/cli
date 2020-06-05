package com.structurizr.publish;

import com.structurizr.Workspace;
import com.structurizr.api.StructurizrClient;
import com.structurizr.documentation.AdrToolsImporter;
import com.structurizr.documentation.AutomaticDocumentationTemplate;
import com.structurizr.encryption.AesEncryptionStrategy;
import com.structurizr.util.StringUtils;
import org.apache.commons.cli.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class StructurizrPublishApplication implements CommandLineRunner {

	@Override
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

		option = new Option("docs", "documentation", true, "Path to documentation");
		option.setRequired(false);
		options.addOption(option);

		option = new Option("adrs", "decisions", true, "Path to decisions");
		option.setRequired(false);
		options.addOption(option);

		option = new Option("passphrase", "passphrase", true, "Client-side encryption passphrase");
		option.setRequired(false);
		options.addOption(option);

		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();

		String apiUrl = "";
		long workspaceId = 1;
		String apiKey = "";
		String apiSecret = "";
		String documentationPath = "";
		String decisionsPath = "";
		String passphrase = "";

		try {
			CommandLine cmd = parser.parse(options, args);

			apiUrl = cmd.getOptionValue("structurizrApiUrl", "https://api.structurizr.com");
			workspaceId = Long.parseLong(cmd.getOptionValue("workspaceId"));
			apiKey = cmd.getOptionValue("apiKey");
			apiSecret = cmd.getOptionValue("apiSecret");
			documentationPath = cmd.getOptionValue("docs");
			decisionsPath = cmd.getOptionValue("adrs");
			passphrase = cmd.getOptionValue("passphrase");
		} catch (ParseException e) {
			System.out.println(e.getMessage());
			formatter.printHelp("structurizr-publish", options);

			System.exit(1);
		}

		StructurizrClient structurizrClient = new StructurizrClient(apiUrl, apiKey, apiSecret);
		structurizrClient.setWorkspaceArchiveLocation(null);
		structurizrClient.setMergeFromRemote(false);

		if (!StringUtils.isNullOrEmpty(passphrase)) {
			structurizrClient.setEncryptionStrategy(new AesEncryptionStrategy(passphrase));
		}

		Workspace workspace = structurizrClient.getWorkspace(workspaceId);
		workspace.setRevision(null);

		AutomaticDocumentationTemplate template = new AutomaticDocumentationTemplate(workspace);
		workspace.getDocumentation().clear();

		if (!StringUtils.isNullOrEmpty(documentationPath)) {
			File path = new File(documentationPath);
			if (!path.exists()) {
				System.out.println("Documentation path " + documentationPath + " does not exist.");
				System.exit(1);
			}

			if (!path.isDirectory()) {
				System.out.println("Documentation path " + documentationPath + " is not a directory.");
				System.exit(1);
			}

			template.addSections(path);
			template.addImages(path);
		}

		if (!StringUtils.isNullOrEmpty(decisionsPath)) {
			File path = new File(decisionsPath);
			if (!path.exists()) {
				System.out.println("Decisions path " + decisionsPath + " does not exist.");
				System.exit(1);
			}

			if (!path.isDirectory()) {
				System.out.println("Decisions path " + decisionsPath + " is not a directory.");
				System.exit(1);
			}

			AdrToolsImporter adrToolsImporter = new AdrToolsImporter(workspace, path);
			adrToolsImporter.importArchitectureDecisionRecords();
		}

		structurizrClient.putWorkspace(workspaceId, workspace);
	}

	public static void main(String[] args) {
		SpringApplication.run(StructurizrPublishApplication.class, args);
	}

}