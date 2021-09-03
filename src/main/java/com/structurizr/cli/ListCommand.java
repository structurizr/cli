package com.structurizr.cli;

import com.structurizr.Workspace;
import com.structurizr.model.DeploymentNode;
import com.structurizr.model.Element;
import com.structurizr.model.ModelItem;
import org.apache.commons.cli.*;

import java.util.Comparator;

class ListCommand extends AbstractCommand {

    private static final String ELEMENT_TYPE = "element";
    private static final Comparator<Element> ELEMENT_COMPARATOR = Comparator.comparing(Element::getName);

    ListCommand() {
    }

    void run(String... args) throws Exception {
        Options options = new Options();

        Option option = new Option("w", "workspace", true, "Path or URL to the workspace JSON file/DSL file(s)");
        option.setRequired(true);
        options.addOption(option);

//        option = new Option("t", "type", true, "The type of items to list");
//        option.setRequired(false);
//        options.addOption(option);

        CommandLineParser commandLineParser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();

        String workspacePathAsString = null;
        String type = null;

        try {
            CommandLine cmd = commandLineParser.parse(options, args);

            workspacePathAsString = cmd.getOptionValue("workspace");
            type = ELEMENT_TYPE; // cmd.getOptionValue("type", "element");
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("list", options);

            System.exit(1);
        }

        Workspace workspace = loadWorkspace(workspacePathAsString);

        if (ELEMENT_TYPE.equalsIgnoreCase(type)) {
            workspace.getModel().getPeople().stream().sorted(ELEMENT_COMPARATOR).forEach(p -> write(p, 0));

            workspace.getModel().getSoftwareSystems().stream().sorted(ELEMENT_COMPARATOR).forEach(softwareSystem -> {
                write(softwareSystem, 0);

                softwareSystem.getContainers().stream().sorted(ELEMENT_COMPARATOR).forEach(container -> {
                    write(container, 1);

                    container.getComponents().stream().sorted(ELEMENT_COMPARATOR).forEach(component -> {
                        write(component, 2);
                    });
                });
            });

            workspace.getModel().getDeploymentNodes().stream().sorted(ELEMENT_COMPARATOR).forEach(p -> writeDeploymentNode(p, 0));
        }
    }

    private void writeDeploymentNode(DeploymentNode deploymentNode, int indent) {
        write(deploymentNode, indent);

        deploymentNode.getInfrastructureNodes().stream().sorted(ELEMENT_COMPARATOR).forEach(in -> write(in, indent+1));
        deploymentNode.getSoftwareSystemInstances().stream().sorted(ELEMENT_COMPARATOR).forEach(in -> write(in, indent+1));
        deploymentNode.getContainerInstances().stream().sorted(ELEMENT_COMPARATOR).forEach(in -> write(in, indent+1));
        deploymentNode.getChildren().stream().sorted(ELEMENT_COMPARATOR).forEach(in -> writeDeploymentNode(in, indent+1));
    }

    private void write(ModelItem modelItem, int indent) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            buf.append("  ");
        }

        System.out.println(buf.toString() + " - " + modelItem.getCanonicalName());
    }

}