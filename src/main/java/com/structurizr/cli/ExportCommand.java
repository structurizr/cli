package com.structurizr.cli;

import com.structurizr.Workspace;
import com.structurizr.api.StructurizrClient;
import com.structurizr.dsl.StructurizrDslParser;
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

class ExportCommand extends AbstractCommand {

    private static final String JSON_FORMAT = "json";
    private static final String PLANTUML_FORMAT = "plantuml";
    private static final String WEBSEQUENCEDIAGRAMS_FORMAT = "websequencediagrams";

    ExportCommand(String version) {
        super(version);
    }

    void run(String... args) throws Exception {
        Options options = new Options();

        Option option = new Option("w", "workspace", true, "Path to Structurizr workspace file (JSON or DSL)");
        option.setRequired(true);
        options.addOption(option);

        option = new Option("f", "format", true, String.format("Export format: %s|%s|%s", PLANTUML_FORMAT, WEBSEQUENCEDIAGRAMS_FORMAT, JSON_FORMAT));
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

        Workspace workspace;

        System.out.println("Exporting workspace from " + workspacePath.getCanonicalPath());

        if (workspacePath.getName().endsWith(".json")) {
            System.out.println(" - loading workspace from JSON");
            
            workspace = WorkspaceUtils.loadWorkspaceFromJson(workspacePath);
        } else {
            System.out.println(" - loading workspace from DSL");
            StructurizrDslParser structurizrDslParser = new StructurizrDslParser();
            structurizrDslParser.parse(workspacePath);

            workspace = structurizrDslParser.getWorkspace();
        }

        workspaceId = workspace.getId();

        addDefaultViewsAndStyles(workspace);

        if (JSON_FORMAT.equalsIgnoreCase(format)) {
            File file = new File(workspacePath.getParent(), String.format("%s.json", prefix(workspaceId)));
            System.out.println(" - writing " + file.getCanonicalPath());
            WorkspaceUtils.saveWorkspaceToJson(workspace, file);
        } else if (PLANTUML_FORMAT.equalsIgnoreCase(format)) {
            if (workspace.getViews().isEmpty()) {
                System.out.println(" - the workspace contains no views");
            } else {
                PlantUMLWriter plantUMLWriter = new PlantUMLWriter();
                plantUMLWriter.setUseSequenceDiagrams(true);
                Collection<PlantUMLDiagram> diagrams = plantUMLWriter.toPlantUMLDiagrams(workspace);

                for (PlantUMLDiagram diagram : diagrams) {
                    File file = new File(workspacePath.getParent(), String.format("%s-%s.puml", prefix(workspaceId), diagram.getKey()));
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
                    File file = new File(workspacePath.getParent(), String.format("%s-%s.wsd", prefix(workspaceId), dynamicView.getKey()));
                    writeToFile(file, definition);
                }

            }
        } else {
            System.out.println(" - unknown output format: " + format);
        }

        System.out.println(" - finished");
    }

    private String prefix(long workspaceId) {
        if (workspaceId > 0) {
            return "structurizr-" + workspaceId;
        } else {
            return "structurizr";
        }
    }

    private void writeToFile(File file, String content) throws Exception {
        System.out.println(" - writing " + file.getCanonicalPath());

        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(content);
        writer.close();
    }

}