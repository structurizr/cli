package com.structurizr.cli;

import com.structurizr.Workspace;
import com.structurizr.util.WorkspaceUtils;
import org.apache.commons.cli.*;

import java.io.File;

class ValidateCommand extends AbstractCommand {

    ValidateCommand() {
    }

    void run(String... args) throws Exception {
        Options options = new Options();

        Option option = new Option("w", "workspace", true, "Path or URL to the workspace JSON file/DSL file(s)");
        option.setRequired(true);
        options.addOption(option);

        CommandLineParser commandLineParser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        String workspacePathAsString = null;

        try {
            CommandLine cmd = commandLineParser.parse(options, args);

            workspacePathAsString = cmd.getOptionValue("workspace");
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("validate", options);

            System.exit(1);
        }

        System.out.println("Validating workspace at " + workspacePathAsString);

        Workspace workspace = loadWorkspace(workspacePathAsString);
        WorkspaceUtils.fromJson(WorkspaceUtils.toJson(workspace, false)); // this will trigger the deserialization validation

        System.out.println(" - validated");
        System.out.println(" - finished");
    }

}