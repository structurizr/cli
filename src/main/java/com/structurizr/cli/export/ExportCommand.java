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
import io.github.goto1134.structurizr.export.d2.D2Exporter;
import org.apache.commons.cli.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ExportCommand extends AbstractCommand {

    private static final String PLUGINS_DIRECTORY_NAME = "plugins";

    private static final Log log = LogFactory.getLog(ExportCommand.class);

    private static final String JSON_FORMAT = "json";
    private static final String THEME_FORMAT = "theme";
    private static final String PLANTUML_FORMAT = "plantuml";
    private static final String PLANTUML_C4PLANTUML_SUBFORMAT = "c4plantuml";
    private static final String PLANTUML_STRUCTURIZR_SUBFORMAT = "structurizr";
    private static final String WEBSEQUENCEDIAGRAMS_FORMAT = "websequencediagrams";
    private static final String MERMAID_FORMAT = "mermaid";
    private static final String DOT_FORMAT = "dot";
    private static final String ILOGRAPH_FORMAT = "ilograph";
    private static final String D2_FORMAT = "d2";
    private static final String CUSTOM_FORMAT = "fqcn";

    private static final Map<String,Exporter> EXPORTERS = new HashMap<>();

    static {
        EXPORTERS.put(JSON_FORMAT, new JsonWorkspaceExporter());
        EXPORTERS.put(THEME_FORMAT, new JsonWorkspaceThemeExporter());
        EXPORTERS.put(PLANTUML_FORMAT, new StructurizrPlantUMLExporter());
        EXPORTERS.put(PLANTUML_FORMAT + "/" + PLANTUML_STRUCTURIZR_SUBFORMAT, new StructurizrPlantUMLExporter());
        EXPORTERS.put(PLANTUML_FORMAT + "/" + PLANTUML_C4PLANTUML_SUBFORMAT, new C4PlantUMLExporter());
        EXPORTERS.put(MERMAID_FORMAT, new MermaidDiagramExporter());
        EXPORTERS.put(DOT_FORMAT, new DOTExporter());
        EXPORTERS.put(WEBSEQUENCEDIAGRAMS_FORMAT, new WebSequenceDiagramsExporter());
        EXPORTERS.put(ILOGRAPH_FORMAT, new IlographExporter());
        EXPORTERS.put(D2_FORMAT, new D2Exporter());
    }

    public ExportCommand() {
    }

    public void run(String... args) throws Exception {
        Options options = new Options();

        Option option = new Option("w", "workspace", true, "Path or URL to the workspace JSON file/DSL file(s)");
        option.setRequired(true);
        options.addOption(option);

        option = new Option("f", "format", true, String.format("Export format: %s[/%s|%s]|%s|%s|%s|%s|%s|%s|%s", PLANTUML_FORMAT, PLANTUML_STRUCTURIZR_SUBFORMAT, PLANTUML_C4PLANTUML_SUBFORMAT, WEBSEQUENCEDIAGRAMS_FORMAT, MERMAID_FORMAT, DOT_FORMAT, ILOGRAPH_FORMAT, JSON_FORMAT, THEME_FORMAT, CUSTOM_FORMAT));
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
            log.error(e.getMessage());
            formatter.setWidth(150);
            formatter.printHelp("export", options);

            System.exit(1);
        }

        Workspace workspace;

        log.info("Exporting workspace from " + workspacePathAsString);

        if (workspacePathAsString.endsWith(".json")) {
            log.info(" - loading workspace from JSON");

            if (workspacePathAsString.startsWith("http://") || workspacePathAsString.startsWith("https")) {
                String json = readFromUrl(workspacePathAsString);
                workspace = WorkspaceUtils.fromJson(json);
                workspacePath = new File(".");
            } else {
                workspacePath = new File(workspacePathAsString);
                workspace = WorkspaceUtils.loadWorkspaceFromJson(workspacePath);
            }
            
        } else {
            log.info(" - loading workspace from DSL");
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

        if (!JSON_FORMAT.equalsIgnoreCase(format)) {
            // only inline the theme amd create default views if the user wants a diagram export
            ThemeUtils.loadThemes(workspace);
            addDefaultViewsAndStyles(workspace);
        }

        if (outputPath == null) {
            outputPath = new File(workspacePath.getCanonicalPath()).getParent();
        }
        
        File outputDir = new File(outputPath);
        outputDir.mkdirs();

       Exporter exporter = findExporter(format, workspacePath);
        if (exporter == null) {
            log.info(" - unknown export format: " + format);
        } else {
            log.info(" - exporting with " + exporter.getClass().getSimpleName());

            if (exporter instanceof DiagramExporter) {
                DiagramExporter diagramExporter = (DiagramExporter) exporter;

                if (workspace.getViews().isEmpty()) {
                    log.info(" - the workspace contains no views");
                } else {
                    Collection<Diagram> diagrams = diagramExporter.export(workspace);

                    for (Diagram diagram : diagrams) {
                        File file = new File(outputPath, String.format("%s-%s.%s", prefix(workspaceId), diagram.getKey(), diagram.getFileExtension()));
                        writeToFile(file, diagram.getDefinition());

                        if (diagram.getLegend() != null) {
                            file = new File(outputPath, String.format("%s-%s-key.%s", prefix(workspaceId), diagram.getKey(), diagram.getFileExtension()));
                            writeToFile(file, diagram.getLegend().getDefinition());
                        }

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

        log.info(" - finished");
    }

    private Exporter findExporter(String format, File workspacePath) {
        if (EXPORTERS.containsKey(format.toLowerCase())) {
            return EXPORTERS.get(format.toLowerCase());
        }

        try {
            Class<?> clazz = loadClass(format, workspacePath);
            if (Exporter.class.isAssignableFrom(clazz)) {
                return (Exporter) clazz.getDeclaredConstructor().newInstance();
            }
        } catch (ClassNotFoundException e) {
            log.error(" - unknown export format: " + format);
        } catch (Exception e) {
            log.error(" - error creating instance of " + format, e);
        }

        return null;
    }

    private Class loadClass(String fqn, File workspaceFile) throws Exception {
        File pluginsDirectory = new File(workspaceFile.getParent(), PLUGINS_DIRECTORY_NAME);
        URL[] urls = new URL[0];

        if (pluginsDirectory.exists()) {
            File[] jarFiles = pluginsDirectory.listFiles((dir, name) -> name.endsWith(".jar"));
            if (jarFiles != null) {
                urls = new URL[jarFiles.length];
                for (int i = 0; i < jarFiles.length; i++) {
                    System.out.println(jarFiles[i].getAbsolutePath());
                    try {
                        urls[i] = jarFiles[i].toURI().toURL();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        URLClassLoader childClassLoader = new URLClassLoader(urls, getClass().getClassLoader());
        return childClassLoader.loadClass(fqn);
    }

    private String prefix(long workspaceId) {
        if (workspaceId > 0) {
            return "structurizr-" + workspaceId;
        } else {
            return "structurizr";
        }
    }

    private void writeToFile(File file, String content) throws Exception {
        log.info(" - writing " + file.getCanonicalPath());

        BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8);
        writer.write(content);
        writer.flush();
        writer.close();
    }

}
