package com.structurizr.cli;

import com.structurizr.Workspace;
import com.structurizr.importer.documentation.DefaultDocumentationImporter;
import com.structurizr.dsl.StructurizrDslParser;
import com.structurizr.export.plantuml.StructurizrPlantUMLExporter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class HelpCommand extends AbstractCommand {

    private static final Log log = LogFactory.getLog(HelpCommand.class);

    HelpCommand() {
    }

    public void run(String... args) throws Exception {
        String version = getClass().getPackage().getImplementationVersion();
        log.info("structurizr-cli: " + version);

        try {
            log.info("structurizr-java: " + Class.forName(Workspace.class.getCanonicalName()).getPackage().getImplementationVersion());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            log.info("structurizr-dsl: " + Class.forName(StructurizrDslParser.class.getCanonicalName()).getPackage().getImplementationVersion());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            log.info("structurizr-export: " + Class.forName(StructurizrPlantUMLExporter.class.getCanonicalName()).getPackage().getImplementationVersion());
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            log.info("structurizr-import: v" + Class.forName(DefaultDocumentationImporter.class.getCanonicalName()).getPackage().getImplementationVersion());
        } catch (Exception e) {
            e.printStackTrace();
        }

        log.info("");
        log.info("Usage: structurizr push|pull|lock|unlock|export|validate|list|help [options]");
    }

}