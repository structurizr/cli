package com.structurizr.cli;

import com.structurizr.Workspace;
import com.structurizr.util.WorkspaceUtils;
import org.apache.commons.cli.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;

class MergeCommand extends AbstractCommand {

    private static final Log log = LogFactory.getLog(MergeCommand.class);

    public MergeCommand() {
    }

    public void run(String... args) throws Exception {
        Options options = new Options();

        Option option = new Option("w", "workspace", true, "Path or URL to the workspace JSON/DSL file");
        option.setRequired(true);
        options.addOption(option);

        option = new Option("l", "layout", true, "Path or URL to the workspace JSON file that includes layout information");
        option.setRequired(true);
        options.addOption(option);

        option = new Option("o", "output", true, "Path and name of an output file");
        option.setRequired(true);
        options.addOption(option);

        CommandLineParser commandLineParser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        String workspaceWithLayoutPath = null;
        String workspaceWithoutLayoutPath = null;
        String outputPath = null;

        try {
            CommandLine cmd = commandLineParser.parse(options, args);

            workspaceWithoutLayoutPath = cmd.getOptionValue("workspace");
            workspaceWithLayoutPath = cmd.getOptionValue("layout");
            outputPath = cmd.getOptionValue("output");

        } catch (ParseException e) {
            log.error(e.getMessage());
            formatter.setWidth(150);
            formatter.printHelp("merge", options);

            System.exit(1);
        }

        log.info("Merging layout");
        log.info(" - loading workspace from " + workspaceWithoutLayoutPath);
        Workspace workspaceWithoutLayout = loadWorkspace(workspaceWithoutLayoutPath);

        log.info(" - loading layout information from " + workspaceWithLayoutPath);
        Workspace workspaceWithLayout = loadWorkspace(workspaceWithLayoutPath);

        log.info(" - merging layout information");
        workspaceWithoutLayout.getViews().copyLayoutInformationFrom(workspaceWithLayout.getViews());
        workspaceWithoutLayout.getViews().getConfiguration().copyConfigurationFrom(workspaceWithLayout.getViews().getConfiguration());

        File outputFile = new File(outputPath);
        log.info(" - writing " + outputFile.getCanonicalPath());
        WorkspaceUtils.saveWorkspaceToJson(workspaceWithoutLayout, outputFile);

        log.info(" - finished");
    }

}