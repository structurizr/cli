package com.structurizr.cli;

import com.structurizr.Workspace;
import com.structurizr.cli.util.Version;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class VersionCommand extends AbstractCommand {

    private static final Log log = LogFactory.getLog(VersionCommand.class);

    VersionCommand() {
    }

    public void run(String... args) throws Exception {
        log.info("structurizr-cli: " + new Version().getBuildNumber());

        try {
            log.info("structurizr-java: " + Class.forName(Workspace.class.getCanonicalName()).getPackage().getImplementationVersion());
        } catch (Exception e) {
            e.printStackTrace();
        }

        log.info("Java: " + System.getProperty("java.version") + "/"  + System.getProperty("java.vendor") + " (" + System.getProperty("java.home") + ")");
        log.info("OS: " + System.getProperty("os.name") + " "  + System.getProperty("os.version") + " (" + System.getProperty("os.arch") + ")");
    }

}