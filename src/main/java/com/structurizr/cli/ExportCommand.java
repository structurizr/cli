package com.structurizr.cli;

import com.structurizr.Workspace;
import com.structurizr.api.StructurizrClient;
import com.structurizr.io.plantuml.PlantUMLDiagram;
import com.structurizr.io.plantuml.PlantUMLWriter;
import com.structurizr.io.websequencediagrams.WebSequenceDiagramsWriter;
import com.structurizr.util.WorkspaceUtils;
import com.structurizr.view.DynamicView;
import org.apache.commons.cli.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Collection;

class ExportCommand {

    private static final String PLANTUML_FORMAT = "plantuml";
    private static final String WEBSEQUENCEDIAGRAMS_FORMAT = "websequencediagrams";

    void run(String... args) throws Exception {
        Options options = new Options();

        Option option = new Option("w", "workspace", true, "Path to Structurizr JSON workspace file");
        option.setRequired(true);
        options.addOption(option);

        option = new Option("f", "format", true, String.format("Export format: %s|%s", PLANTUML_FORMAT, WEBSEQUENCEDIAGRAMS_FORMAT));
        option.setRequired(true);
        options.addOption(option);

        CommandLineParser commandLineParser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        File workspacePath = null;
        long workspaceId = 1;
        String format = "";

        try {
            CommandLine cmd = commandLineParser.parse(options, args);

            workspacePath = new File(cmd.getOptionValue("workspace"));
            format = cmd.getOptionValue("format");

        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("export", options);

            System.exit(1);
        }

        System.out.println("Exporting workspace from " + workspacePath.getCanonicalPath());
        System.out.println(" - loading workspace from JSON");
        Workspace workspace = WorkspaceUtils.loadWorkspaceFromJson(workspacePath);
        workspaceId = workspace.getId();

        if (PLANTUML_FORMAT.equalsIgnoreCase(format)) {
            if (workspace.getViews().isEmpty()) {
                System.out.println(" - the workspace contains no views");
            } else {
                PlantUMLWriter plantUMLWriter = new PlantUMLWriter();
                Collection<PlantUMLDiagram> diagrams = plantUMLWriter.toPlantUMLDiagrams(workspace);

                for (PlantUMLDiagram diagram : diagrams) {
                    File file = new File(workspacePath.getParent(), String.format("structurizr-%s-%s.puml", workspaceId, diagram.getKey()));
                    writeToFile(file, diagram.getDefinition());
                }
            }
        } else if (WEBSEQUENCEDIAGRAMS_FORMAT.equalsIgnoreCase(format)) {
            WebSequenceDiagramsWriter webSequenceDiagramsWriter = new WebSequenceDiagramsWriter();
            if (workspace.getViews().getDynamicViews().isEmpty()) {
                System.out.println(" - the workspace contains no dynamic views");
            } else {
                for (DynamicView dynamicView : workspace.getViews().getDynamicViews()) {
                    String definition = webSequenceDiagramsWriter.toString(dynamicView);
                    File file = new File(workspacePath.getParent(), String.format("structurizr-%s-%s.wsd", workspaceId, dynamicView.getKey()));
                    writeToFile(file, definition);
                }

            }
        }

        System.out.println(" - finished");
    }

    private void writeToFile(File file, String content) throws Exception {
        System.out.println(" - writing " + file.getCanonicalPath());

        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(content);
        writer.close();
    }

}