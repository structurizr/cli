package com.structurizr.cli;

import com.structurizr.Workspace;
import com.structurizr.dsl.StructurizrDslParser;
import com.structurizr.export.plantuml.StructurizrPlantUMLExporter;
import com.structurizr.importer.documentation.DefaultDocumentationImporter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

class VersionCommand extends AbstractCommand {

    private static final Log log = LogFactory.getLog(VersionCommand.class);

    VersionCommand() {
    }

    private static final String BUILD_VERSION_KEY = "build.number";
    private static final String BUILD_TIMESTAMP_KEY = "build.timestamp";
    private static final String GIT_COMMIT_KEY = "git.commit";

    private static final String ISO_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    public void run(String... args) throws Exception {
        String version = "";
        String buildTimestamp = "";
        String gitCommit = "";

        try {
            Properties buildProperties = new Properties();
            InputStream in = VersionCommand.class.getClassLoader().getResourceAsStream("build.properties");
            DateFormat format = new SimpleDateFormat(ISO_DATE_TIME_FORMAT);
            if (in != null) {
                buildProperties.load(in);
                version = buildProperties.getProperty(BUILD_VERSION_KEY);
                buildTimestamp = buildProperties.getProperty(BUILD_TIMESTAMP_KEY);
                gitCommit = buildProperties.getProperty(GIT_COMMIT_KEY);
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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

        log.info("Java: " + System.getProperty("java.version") + "/"  + System.getProperty("java.vendor") + " (" + System.getProperty("java.home") + ")");
        log.info("OS: " + System.getProperty("os.name") + " "  + System.getProperty("os.version") + " (" + System.getProperty("os.arch") + ")");
    }

}