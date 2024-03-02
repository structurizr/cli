package com.structurizr.cli;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

class HelpCommand extends AbstractCommand {

    private static final Log log = LogFactory.getLog(HelpCommand.class);

    HelpCommand() {
    }

    public void run(String... args) throws Exception {
        log.info("Usage: structurizr push|pull|lock|unlock|export|merge|validate|inspect|list|version|help [options]");
    }

}