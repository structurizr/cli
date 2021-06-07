package com.structurizr.cli;

import com.structurizr.Workspace;
import com.structurizr.dsl.StructurizrDslFormatter;
import com.structurizr.dsl.StructurizrDslParser;
import com.structurizr.io.Diagram;
import com.structurizr.io.dot.DOTExporter;
import com.structurizr.io.ilograph.IlographExporter;
import com.structurizr.io.mermaid.MermaidDiagramExporter;
import com.structurizr.io.plantuml.AbstractPlantUMLExporter;
import com.structurizr.io.plantuml.C4PlantUMLExporter;
import com.structurizr.io.plantuml.StructurizrPlantUMLExporter;
import com.structurizr.io.websequencediagrams.WebSequenceDiagramsExporter;
import com.structurizr.util.WorkspaceUtils;
import com.structurizr.view.DynamicView;
import com.structurizr.view.ThemeUtils;
import org.apache.commons.cli.*;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collection;

class ExportCommand extends AbstractCommand {

    private static final String JSON_FORMAT = "json";
    private static final String THEME_FORMAT = "theme";
    private static final String DSL_FORMAT = "dsl";
    private static final String PLANTUML_FORMAT = "plantuml";
    private static final String PLANTUML_C4PLANTUML_SUBFORMAT = "c4plantuml";
    private static final String PLANTUML_STRUCTURIZR_SUBFORMAT = "structurizr";
    private static final String WEBSEQUENCEDIAGRAMS_FORMAT = "websequencediagrams";
    private static final String MERMAID_FORMAT = "mermaid";
    private static final String DOT_FORMAT = "dot";
    private static final String ILOGRAPH_FORMAT = "ilograph";

    ExportCommand(String version) {
        super(version);
    }

    void run(String... args) throws Exception {
        Options options = new Options();

        Option option = new Option("w", "workspace", true, "Path or URL to the workspace JSON file/DSL file(s)");
        option.setRequired(true);
        options.addOption(option);

        option = new Option("f", "format", true, String.format("Export format: %s[/%s|%s]|%s|%s|%s|%s|%s|%s|%s", PLANTUML_FORMAT, PLANTUML_STRUCTURIZR_SUBFORMAT, PLANTUML_C4PLANTUML_SUBFORMAT, WEBSEQUENCEDIAGRAMS_FORMAT, MERMAID_FORMAT, DOT_FORMAT, ILOGRAPH_FORMAT, JSON_FORMAT, DSL_FORMAT, THEME_FORMAT));
        option.setRequired(true);
        options.addOption(option);

        option = new Option("o", "output", true, "Path to an output directory");
        option.setRequired(false);
        options.addOption(option);

        option = new Option("a", "animation", true, "Export animation (default: false)");
        option.setRequired(false);
        options.addOption(option);

        CommandLineParser commandLineParser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        String workspacePathAsString = null;
        File workspacePath = null;
        long workspaceId = 1;
        String format = "";
        boolean animation = false;
        String outputPath = null;

        try {
            CommandLine cmd = commandLineParser.parse(options, args);

            workspacePathAsString = cmd.getOptionValue("workspace");
            format = cmd.getOptionValue("format").toLowerCase();
            animation = Boolean.parseBoolean(cmd.getOptionValue("animation", "false"));
            outputPath = cmd.getOptionValue("output");

        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.setWidth(150);
            formatter.printHelp("export", options);

            System.exit(1);
        }

        Workspace workspace;

        System.out.println("Exporting workspace from " + workspacePathAsString);

        if (workspacePathAsString.endsWith(".json")) {
            System.out.println(" - loading workspace from JSON");

            if (workspacePathAsString.startsWith("http://") || workspacePathAsString.startsWith("https")) {
                String json = readFromUrl(workspacePathAsString);
                workspace = WorkspaceUtils.fromJson(json);
                workspacePath = new File(".");
            } else {
                workspacePath = new File(workspacePathAsString);
                workspace = WorkspaceUtils.loadWorkspaceFromJson(workspacePath);
            }
            
        } else {
            System.out.println(" - loading workspace from DSL");
            StructurizrDslParser structurizrDslParser = new StructurizrDslParser();

            if (workspacePathAsString.startsWith("http://") || workspacePathAsString.startsWith("https")) {
                String dsl = readFromUrl(workspacePathAsString);
                structurizrDslParser.parse(dsl);
                workspacePath = new File(".");
            } else {
                workspacePath = new File(workspacePathAsString);
                structurizrDslParser.parse(workspacePath);
            }

            workspace = structurizrDslParser.getWorkspace();
        }

        workspaceId = workspace.getId();

        if (!JSON_FORMAT.equalsIgnoreCase(format) && !DSL_FORMAT.equalsIgnoreCase(format)) {
            // only inline the theme amd create default views if the user wants a diagram export
            ThemeUtils.loadThemes(workspace);
            addDefaultViewsAndStyles(workspace);
        }

        if (outputPath == null) {
            outputPath = new File(workspacePath.getCanonicalPath()).getParent();
        }
        
        File outputDir = new File(outputPath);
        outputDir.mkdirs();

        if (JSON_FORMAT.equalsIgnoreCase(format)) {
            String filename = workspacePath.getName().substring(0, workspacePath.getName().lastIndexOf('.'));
            File file = new File(outputPath, String.format("%s.json", filename));
            System.out.println(" - writing " + file.getCanonicalPath());
            WorkspaceUtils.saveWorkspaceToJson(workspace, file);
        } else if (THEME_FORMAT.equalsIgnoreCase(format)) {
            String filename = workspacePath.getName().substring(0, workspacePath.getName().lastIndexOf('.'));
            File file = new File(outputPath, String.format("%s-theme.json", filename));
            System.out.println(" - writing " + file.getCanonicalPath());
            ThemeUtils.toJson(workspace, file);
        } else if (DSL_FORMAT.equalsIgnoreCase(format)) {
            String filename = workspacePath.getName().substring(0, workspacePath.getName().lastIndexOf('.'));
            File file = new File(outputPath, String.format("%s.dsl", filename));

            StructurizrDslFormatter structurizrDslFormatter = new StructurizrDslFormatter();
            String dsl = structurizrDslFormatter.format(WorkspaceUtils.toJson(workspace, false));

            writeToFile(file, dsl);
        } else if (format.startsWith(PLANTUML_FORMAT)) {
            AbstractPlantUMLExporter plantUMLExporter = null;
            boolean useSequenceDiagrams = true;

            String[] tokens = format.split("/");
            String subformat = PLANTUML_STRUCTURIZR_SUBFORMAT;
            if (tokens.length == 2) {
                subformat = tokens[1];
            }

            switch (subformat) {
                case PLANTUML_C4PLANTUML_SUBFORMAT:
                    plantUMLExporter = new C4PlantUMLExporter();
                    useSequenceDiagrams = false;
                    break;
                case PLANTUML_STRUCTURIZR_SUBFORMAT:
                    plantUMLExporter = new StructurizrPlantUMLExporter();
                    useSequenceDiagrams = true;
                    break;
                default:
                    System.out.println(" - unknown PlantUML subformat: " + subformat);
                    System.exit(1);
            }

            System.out.println(" - using " + plantUMLExporter.getClass().getSimpleName());

            if (workspace.getViews().isEmpty()) {
                System.out.println(" - the workspace contains no views");
            } else {
                plantUMLExporter.setUseSequenceDiagrams(false);
                Collection<Diagram> diagrams = plantUMLExporter.export(workspace);

                for (Diagram diagram : diagrams) {
                    File file = new File(outputPath, String.format("%s-%s.puml", prefix(workspaceId), diagram.getKey()));
                    writeToFile(file, diagram.getDefinition());

                    if (!diagram.getFrames().isEmpty()) {
                        int index = 1;
                        for (Diagram frame : diagram.getFrames()) {
                            file = new File(outputPath, String.format("%s-%s-%s.puml", prefix(workspaceId), diagram.getKey(), index));
                            writeToFile(file, frame.getDefinition());
                            index++;
                        }
                    }
                }

                if (useSequenceDiagrams) {
                    plantUMLExporter.setUseSequenceDiagrams(true);
                    for (DynamicView dynamicView : workspace.getViews().getDynamicViews()) {
                        Diagram diagram = plantUMLExporter.export(dynamicView);

                        File file = new File(outputPath, String.format("%s-%s-sequence.puml", prefix(workspaceId), dynamicView.getKey()));
                        writeToFile(file, diagram.getDefinition());
                    }
                }
            }
        } else if (MERMAID_FORMAT.equalsIgnoreCase(format)) {
            if (workspace.getViews().isEmpty()) {
                System.out.println(" - the workspace contains no views");
            } else {
                MermaidDiagramExporter mermaidDiagramExporter = new MermaidDiagramExporter();
                Collection<Diagram> diagrams = mermaidDiagramExporter.export(workspace);

                for (Diagram diagram : diagrams) {
                    File file = new File(outputPath, String.format("%s-%s.mmd", prefix(workspaceId), diagram.getKey()));
                    writeToFile(file, diagram.getDefinition());
                }
            }
        } else if (WEBSEQUENCEDIAGRAMS_FORMAT.equalsIgnoreCase(format)) {
            WebSequenceDiagramsExporter webSequenceDiagramsExporter = new WebSequenceDiagramsExporter();
            if (workspace.getViews().getDynamicViews().isEmpty()) {
                System.out.println(" - the workspace contains no dynamic views");
            } else {
                for (DynamicView dynamicView : workspace.getViews().getDynamicViews()) {
                    Diagram diagram = webSequenceDiagramsExporter.export(dynamicView);
                    File file = new File(outputPath, String.format("%s-%s.wsd", prefix(workspaceId), dynamicView.getKey()));
                    writeToFile(file, diagram.getDefinition());
                }
            }
        } else if (DOT_FORMAT.equalsIgnoreCase(format)) {
            if (workspace.getViews().isEmpty()) {
                System.out.println(" - the workspace contains no views");
            } else {
                DOTExporter dotExporter = new DOTExporter();
                Collection<Diagram> diagrams = dotExporter.export(workspace);

                for (Diagram diagram : diagrams) {
                    File file = new File(outputPath, String.format("%s-%s.dot", prefix(workspaceId), diagram.getKey()));
                    writeToFile(file, diagram.getDefinition());
                }
            }
        } else if (ILOGRAPH_FORMAT.equalsIgnoreCase(format)) {
            String ilographDefinition = new IlographExporter().export(workspace);
            File file = new File(outputPath, String.format("%s.idl", prefix(workspaceId)));
            writeToFile(file, ilographDefinition);
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

        BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8);
        writer.write(content);
        writer.close();
    }

}