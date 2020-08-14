package com.structurizr.cli;

import com.structurizr.Workspace;
import com.structurizr.dsl.StructurizrDslParser;
import com.structurizr.io.ilograph.IlographWriter;
import com.structurizr.io.mermaid.MermaidDiagram;
import com.structurizr.io.mermaid.MermaidWriter;
import com.structurizr.io.plantuml.PlantUMLDiagram;
import com.structurizr.io.plantuml.StructurizrPlantUMLWriter;
import com.structurizr.io.websequencediagrams.WebSequenceDiagramsWriter;
import com.structurizr.util.ThemeUtils;
import com.structurizr.util.WorkspaceUtils;
import com.structurizr.view.DynamicView;
import org.apache.commons.cli.*;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Collection;

class ExportCommand extends AbstractCommand {

    private static final int HTTP_OK_STATUS = 200;

    private static final String JSON_FORMAT = "json";
    private static final String PLANTUML_FORMAT = "plantuml";
    private static final String WEBSEQUENCEDIAGRAMS_FORMAT = "websequencediagrams";
    private static final String MERMAID_FORMAT = "mermaid";
    private static final String ILOGRAPH_FORMAT = "ilograph";

    ExportCommand(String version) {
        super(version);
    }

    void run(String... args) throws Exception {
        Options options = new Options();

        Option option = new Option("w", "workspace", true, "Path or URL to the workspace JSON/DSL file");
        option.setRequired(true);
        options.addOption(option);

        option = new Option("f", "format", true, String.format("Export format: %s|%s|%s|%s", PLANTUML_FORMAT, WEBSEQUENCEDIAGRAMS_FORMAT, MERMAID_FORMAT, JSON_FORMAT));
        option.setRequired(true);
        options.addOption(option);

        CommandLineParser commandLineParser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        String workspacePathAsString = null;
        File workspacePath = null;
        long workspaceId = 1;
        String format = "";

        try {
            CommandLine cmd = commandLineParser.parse(options, args);

            workspacePathAsString = cmd.getOptionValue("workspace");
            format = cmd.getOptionValue("format");

        } catch (ParseException e) {
            System.out.println(e.getMessage());
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

        ThemeUtils.loadStylesFromThemes(workspace);
        addDefaultViewsAndStyles(workspace);

        if (JSON_FORMAT.equalsIgnoreCase(format)) {
            File file = new File(workspacePath.getParent(), String.format("%s.json", prefix(workspaceId)));
            System.out.println(" - writing " + file.getCanonicalPath());
            WorkspaceUtils.saveWorkspaceToJson(workspace, file);
        } else if (PLANTUML_FORMAT.equalsIgnoreCase(format)) {
            if (workspace.getViews().isEmpty()) {
                System.out.println(" - the workspace contains no views");
            } else {
                StructurizrPlantUMLWriter plantUMLWriter = new StructurizrPlantUMLWriter();
                plantUMLWriter.setUseSequenceDiagrams(false);
                Collection<PlantUMLDiagram> diagrams = plantUMLWriter.toPlantUMLDiagrams(workspace);

                for (PlantUMLDiagram diagram : diagrams) {
                    File file = new File(workspacePath.getParent(), String.format("%s-%s.puml", prefix(workspaceId), diagram.getKey()));
                    writeToFile(file, diagram.getDefinition());
                }

                plantUMLWriter.setUseSequenceDiagrams(true);
                for (DynamicView dynamicView : workspace.getViews().getDynamicViews()) {
                    String definition = plantUMLWriter.toString(dynamicView);

                    File file = new File(workspacePath.getParent(), String.format("%s-%s-sequence.puml", prefix(workspaceId), dynamicView.getKey()));
                    writeToFile(file, definition);
                }
            }
        } else if (MERMAID_FORMAT.equalsIgnoreCase(format)) {
            if (workspace.getViews().isEmpty()) {
                System.out.println(" - the workspace contains no views");
            } else {
                MermaidWriter mermaidWriter = new MermaidWriter();
                mermaidWriter.setUseSequenceDiagrams(false);
                Collection<MermaidDiagram> diagrams = mermaidWriter.toMermaidDiagrams(workspace);

                for (MermaidDiagram diagram : diagrams) {
                    File file = new File(workspacePath.getParent(), String.format("%s-%s.mmd", prefix(workspaceId), diagram.getKey()));
                    writeToFile(file, diagram.getDefinition());
                }

                mermaidWriter.setUseSequenceDiagrams(true);
                for (DynamicView dynamicView : workspace.getViews().getDynamicViews()) {
                    String definition = mermaidWriter.toString(dynamicView);

                    File file = new File(workspacePath.getParent(), String.format("%s-%s-sequence.mmd", prefix(workspaceId), dynamicView.getKey()));
                    writeToFile(file, definition);
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
        } else if (ILOGRAPH_FORMAT.equalsIgnoreCase(format)) {
            String ilographDefinition = new IlographWriter().toString(workspace);
            File file = new File(workspacePath.getParent(), String.format("%s.idl", prefix(workspaceId)));
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

        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(content);
        writer.close();
    }

    private String readFromUrl(String url) {
        try (CloseableHttpClient httpClient = HttpClients.createSystem()) {
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpResponse response = httpClient.execute(httpGet);

            if (response.getCode() == HTTP_OK_STATUS) {
                return EntityUtils.toString(response.getEntity());
            }
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }

        return "";
    }

}