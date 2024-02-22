package com.structurizr.cli;

import com.structurizr.Workspace;
import com.structurizr.inspection.Inspector;
import com.structurizr.inspection.Severity;
import com.structurizr.inspection.Violation;
import com.structurizr.util.StringUtils;
import org.apache.commons.cli.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class InspectCommand extends AbstractCommand {

    private static final Log log = LogFactory.getLog(InspectCommand.class);

    private static final String DEFAULT_INSPECTOR = "com.structurizr.inspection.DefaultInspector";

    InspectCommand() {
    }

    public void run(String... args) throws Exception {
        Options options = new Options();

        Option option = new Option("w", "workspace", true, "Path or URL to the workspace JSON/DSL file");
        option.setRequired(true);
        options.addOption(option);

        option = new Option("i", "inspector", true, "Inspector implementation to use");
        option.setRequired(false);
        options.addOption(option);

        option = new Option("s", "severity", true, "A comma separated list of the severity level(s) to show");
        option.setRequired(false);
        options.addOption(option);

        CommandLineParser commandLineParser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        String workspacePathAsString = null;
        String inspectorName = null;
        String severitiesAsString = null;

        try {
            CommandLine cmd = commandLineParser.parse(options, args);

            workspacePathAsString = cmd.getOptionValue("workspace");
            inspectorName = cmd.getOptionValue("inspector");
            severitiesAsString = cmd.getOptionValue("severity");
        } catch (ParseException e) {
            log.error(e.getMessage());
            formatter.printHelp("inspect", options);

            System.exit(1);
        }

        if (StringUtils.isNullOrEmpty(inspectorName)) {
            inspectorName = DEFAULT_INSPECTOR;
        }

        log.debug("Inspecting workspace at " + workspacePathAsString + " using " + inspectorName);

        Set<Severity> severities = new HashSet<>();
        if (StringUtils.isNullOrEmpty(severitiesAsString)) {
            severities.add(Severity.ERROR);
            severities.add(Severity.WARNING);
            severities.add(Severity.INFO);
            severities.add(Severity.IGNORE);
        } else {
            for (String severity : severitiesAsString.split(",")) {
                severities.add(Severity.valueOf(severity.trim().toUpperCase()));
            }
        }

        try {
            Workspace workspace = loadWorkspace(workspacePathAsString);
            Inspector inspector = findInspector(inspectorName, workspace, new File(workspacePathAsString));

            if (inspector != null) {
                List<Violation> violations = inspector.getViolations();
                violations.sort(Comparator.comparing(Violation::getSeverity));

                violations = violations.stream().filter(v -> severities.contains(v.getSeverity())).collect(Collectors.toList());

                if (!violations.isEmpty()) {
                    int typeColumns = 0;
                    int descriptionColumns = 0;
                    for (Violation violation : violations) {
                        typeColumns = Math.max(typeColumns, violation.getType().length());
                        descriptionColumns = Math.max(descriptionColumns, violation.getMessage().length());
                    }

                    String rowFormat = "%-6s | %-" + typeColumns + "s | %s";

                    int counter = 0;
                    for (Violation violation : violations) {
                        if (severities.contains(violation.getSeverity())) {
                            counter++;

                            String line = String.format(
                                    rowFormat,
                                    violation.getSeverity().toString(),
                                    violation.getType(),
                                    violation.getMessage()
                            );

                            log.info(line);
                        }
                    }

                    System.exit(counter); // non-zero if there are violations shown
                }
            }
        } catch (Exception e) {
            // print the error and exit
            log.error(e.getMessage());
            System.exit(1);
        }

        log.debug(" - inspected");
        log.debug(" - finished");
    }

    private Inspector findInspector(String name, Workspace workspace, File workspacePath) {
        try {
            Class<?> clazz = loadClass(name, workspacePath);
            if (Inspector.class.isAssignableFrom(clazz)) {
                return (Inspector) clazz.getDeclaredConstructor(Workspace.class).newInstance(workspace);
            }
        } catch (ClassNotFoundException e) {
            log.error(" - unknown inspector: " + name);
        } catch (Exception e) {
            log.error(" - error creating instance of " + name, e);
        }

        return null;
    }

}