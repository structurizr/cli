package com.structurizr.cli.export;

import com.structurizr.Workspace;
import com.structurizr.cli.AbstractCommand;
import com.structurizr.dsl.StructurizrDslParser;
import com.structurizr.export.*;
import com.structurizr.export.dot.DOTExporter;
import com.structurizr.export.ilograph.IlographExporter;
import com.structurizr.export.mermaid.MermaidDiagramExporter;
import com.structurizr.export.plantuml.C4PlantUMLExporter;
import com.structurizr.export.plantuml.StructurizrPlantUMLExporter;
import com.structurizr.export.websequencediagrams.WebSequenceDiagramsExporter;
import com.structurizr.util.WorkspaceUtils;
import com.structurizr.view.ThemeUtils;
import org.apache.commons.cli.*;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ExportCommand extends AbstractCommand {

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
    private static final String CUSTOM_FORMAT = "fqcn";

    private static final Map<String,Exporter> EXPORTERS = new HashMap<>();

    static {
        EXPORTERS.put(JSON_FORMAT, new JsonWorkspaceExporter());
        EXPORTERS.put(THEME_FORMAT, new JsonWorkspaceThemeExporter());
        EXPORTERS.put(DSL_FORMAT, new DslWorkspaceExporter());
        EXPORTERS.put(PLANTUML_FORMAT, new StructurizrPlantUMLExporter());
        EXPORTERS.put(PLANTUML_FORMAT + "/" + PLANTUML_STRUCTURIZR_SUBFORMAT, new StructurizrPlantUMLExporter());
        EXPORTERS.put(PLANTUML_FORMAT + "/" + PLANTUML_C4PLANTUML_SUBFORMAT, new C4PlantUMLExporter());
        EXPORTERS.put(MERMAID_FORMAT, new MermaidDiagramExporter());
        EXPORTERS.put(DOT_FORMAT, new DOTExporter());
        EXPORTERS.put(WEBSEQUENCEDIAGRAMS_FORMAT, new WebSequenceDiagramsExporter());
        EXPORTERS.put(ILOGRAPH_FORMAT, new IlographExporter());
    }

    public ExportCommand() {
    }

    public void run(String... args) throws Exception {
        Options options = new Options();

        Option option = new Option("w", "workspace", true, "Path or URL to the workspace JSON file/DSL file(s)");
        option.setRequired(true);
        options.addOption(option);

        option = new Option("f", "format", true, String.format("Export format: %s[/%s|%s]|%s|%s|%s|%s|%s|%s|%s|%s", PLANTUML_FORMAT, PLANTUML_STRUCTURIZR_SUBFORMAT, PLANTUML_C4PLANTUML_SUBFORMAT, WEBSEQUENCEDIAGRAMS_FORMAT, MERMAID_FORMAT, DOT_FORMAT, ILOGRAPH_FORMAT, JSON_FORMAT, DSL_FORMAT, THEME_FORMAT, CUSTOM_FORMAT));
        option.setRequired(true);
        options.addOption(option);

        option = new Option("o", "output", true, "Path to an output directory");
        option.setRequired(false);
        options.addOption(option);

        CommandLineParser commandLineParser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        String workspacePathAsString = null;
        File workspacePath = null;
        long workspaceId = 1;
        String format = "";
        String outputPath = null;

        try {
            CommandLine cmd = commandLineParser.parse(options, args);

            workspacePathAsString = cmd.getOptionValue("workspace");
            format = cmd.getOptionValue("format");
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

       Exporter exporter = findExporter(format);
        if (exporter == null) {
            System.out.println(" - unknown export format: " + format);
        } else {
            System.out.println(" - exporting with " + exporter.getClass().getSimpleName());

            if (exporter instanceof DiagramExporter) {
                DiagramExporter diagramExporter = (DiagramExporter) exporter;

                if (workspace.getViews().isEmpty()) {
                    System.out.println(" - the workspace contains no views");
                } else {
                    Collection<Diagram> diagrams = diagramExporter.export(workspace);

                    for (Diagram diagram : diagrams) {
                        File file = new File(outputPath, String.format("%s-%s.%s", prefix(workspaceId), diagram.getKey(), diagram.getFileExtension()));
                        writeToFile(file, diagram.getDefinition());

                        if (!diagram.getFrames().isEmpty()) {
                            int index = 1;
                            for (Diagram frame : diagram.getFrames()) {
                                file = new File(outputPath, String.format("%s-%s-%s.%s", prefix(workspaceId), diagram.getKey(), index, diagram.getFileExtension()));
                                writeToFile(file, frame.getDefinition());
                                index++;
                            }
                        }
                    }
                }
            } else if (exporter instanceof WorkspaceExporter) {
                WorkspaceExporter workspaceExporter = (WorkspaceExporter) exporter;
                WorkspaceExport export = workspaceExporter.export(workspace);

                String filename;

                if (THEME_FORMAT.equalsIgnoreCase(format)) {
                    filename = workspacePath.getName().substring(0, workspacePath.getName().lastIndexOf('.')) + "-theme";
                } else {
                    filename = workspacePath.getName().substring(0, workspacePath.getName().lastIndexOf('.'));
                }

                File file = new File(outputPath, String.format("%s.%s", filename, export.getFileExtension()));
                writeToFile(file, export.getDefinition());
            }
        }

        System.out.println(" - finished");
    }

    private Exporter findExporter(String format) {
        if (EXPORTERS.containsKey(format.toLowerCase())) {
            return EXPORTERS.get(format.toLowerCase());
        }

        try {
            Class<?> clazz = Class.forName(format);
            if (Exporter.class.isAssignableFrom(clazz)) {
                return (Exporter) clazz.getDeclaredConstructor().newInstance();
            }
        } catch (ClassNotFoundException e) {
            System.out.println(" - unknown export format: " + format);
        } catch (Exception e) {
            System.out.println(" - error creating instance of " + format);
            e.printStackTrace();
        }

        return null;
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
        writer.flush();
        writer.close();
    }

}