package com.structurizr.cli;

import com.structurizr.Workspace;
import com.structurizr.util.StringUtils;
import com.structurizr.util.WorkspaceUtils;
import com.structurizr.view.ModelView;
import com.structurizr.view.View;
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

        option = new Option("v", "view", true, "Key of the view to merge layout information for (optional)");
        option.setRequired(false);
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
        String viewKey = null;
        String outputPath = null;

        try {
            CommandLine cmd = commandLineParser.parse(options, args);

            workspaceWithoutLayoutPath = cmd.getOptionValue("workspace");
            workspaceWithLayoutPath = cmd.getOptionValue("layout");
            viewKey = cmd.getOptionValue("view");
            outputPath = cmd.getOptionValue("output");

        } catch (ParseException e) {
            log.error(e.getMessage());
            formatter.setWidth(150);
            formatter.printHelp("merge", options);

            System.exit(1);
        }

        log.info("Merging layout");

        if (StringUtils.isNullOrEmpty(viewKey)) {
            log.info(" - for all views");
        } else {
            log.info(" - for view \"" + viewKey + "\"");
        }

        log.info(" - loading workspace from " + workspaceWithoutLayoutPath);
        Workspace workspaceWithoutLayout = loadWorkspace(workspaceWithoutLayoutPath);

        log.info(" - loading layout information from " + workspaceWithLayoutPath);
        Workspace workspaceWithLayout = loadWorkspace(workspaceWithLayoutPath);

        log.info(" - merging layout information");
        if (StringUtils.isNullOrEmpty(viewKey)) {
            workspaceWithoutLayout.getViews().copyLayoutInformationFrom(workspaceWithLayout.getViews());
            workspaceWithoutLayout.getViews().getConfiguration().copyConfigurationFrom(workspaceWithLayout.getViews().getConfiguration());
        } else {
            View viewWithoutLayout = workspaceWithoutLayout.getViews().getViewWithKey(viewKey);
            View viewWithLayout = workspaceWithLayout.getViews().getViewWithKey(viewKey);

            if (viewWithoutLayout == null) {
                log.info(" - \"" + viewKey + "\" does not exist in " + workspaceWithoutLayoutPath);
                System.exit(1);
            } else if (!(viewWithoutLayout instanceof ModelView)) {
                log.info(" - \"" + viewKey + "\" is not a model view in " + workspaceWithoutLayoutPath);
                System.exit(1);
            }
            if (viewWithLayout == null) {
                log.info(" - \"" + viewKey + "\" does not exist in " + workspaceWithLayoutPath);
                System.exit(1);
            } else if (!(viewWithLayout instanceof ModelView)) {
                log.info(" - \"" + viewKey + "\" is not a model view in " + workspaceWithLayoutPath);
                System.exit(1);
            }

            ((ModelView)viewWithoutLayout).copyLayoutInformationFrom((ModelView)viewWithLayout);
        }

        File outputFile = new File(outputPath);
        log.info(" - writing " + outputFile.getCanonicalPath());
        WorkspaceUtils.saveWorkspaceToJson(workspaceWithoutLayout, outputFile);

        log.info(" - finished");
    }

}