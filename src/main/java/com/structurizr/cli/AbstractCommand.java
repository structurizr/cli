package com.structurizr.cli;

import com.structurizr.Workspace;
import com.structurizr.model.Container;
import com.structurizr.model.DeploymentNode;
import com.structurizr.model.SoftwareSystem;
import com.structurizr.model.Tags;
import com.structurizr.view.*;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;

abstract class AbstractCommand {

    private String version;

    AbstractCommand(String version) {
        this.version = version;
    }

    String getAgent() {
        return "structurizr-cli/" + version;

    }

    void addDefaultViewsAndStyles(Workspace workspace) {
        if (workspace.getViews().isEmpty()) {
            System.out.println(" - no views defined; creating default views");

            // create a single System Landscape diagram containing all people and software systems
            SystemLandscapeView systemLandscapeView = workspace.getViews().createSystemLandscapeView(generateViewKey(workspace, "SystemLandscape"), "A system landscape view for " + workspace.getName());
            systemLandscapeView.addAllElements();
            systemLandscapeView.enableAutomaticLayout(AutomaticLayout.RankDirection.TopBottom, 300, 300);
            systemLandscapeView.setEnterpriseBoundaryVisible(true);

            // and a system context view plus container view for each software system
            for (SoftwareSystem softwareSystem : workspace.getModel().getSoftwareSystems()) {
                SystemContextView systemContextView = workspace.getViews().createSystemContextView(softwareSystem, generateViewKey(workspace, "SystemContext"), "A system context view for " + softwareSystem.getName() + ".");
                systemContextView.addNearestNeighbours(softwareSystem);
                systemContextView.enableAutomaticLayout(AutomaticLayout.RankDirection.TopBottom, 300, 300);
                systemContextView.setEnterpriseBoundaryVisible(true);

                if (softwareSystem.getContainers().size() > 0) {
                    ContainerView containerView = workspace.getViews().createContainerView(softwareSystem, generateViewKey(workspace, "Container"), "A container view for " + softwareSystem.getName() + ".");
                    softwareSystem.getContainers().forEach(containerView::addNearestNeighbours);
                    containerView.enableAutomaticLayout(AutomaticLayout.RankDirection.TopBottom, 300, 300);
                    containerView.setExternalSoftwareSystemBoundariesVisible(true);

                    for (Container container : softwareSystem.getContainers()) {
                        if (container.getComponents().size() > 0) {
                            ComponentView componentView = workspace.getViews().createComponentView(container, generateViewKey(workspace, "Component"), "A component view for " + container.getName() + ".");
                            container.getComponents().forEach(componentView::addNearestNeighbours);
                            componentView.enableAutomaticLayout(AutomaticLayout.RankDirection.TopBottom, 300, 300);
                            componentView.setExternalSoftwareSystemBoundariesVisible(true);
                        }
                    }
                }
            }

            // and deployment views for each environment
            Set<String> deploymentEnvironments = new HashSet<>();
            for (DeploymentNode deploymentNode : workspace.getModel().getDeploymentNodes()) {
                deploymentEnvironments.add(deploymentNode.getEnvironment());
            }

            for (String deploymentEnvironment : deploymentEnvironments) {
                DeploymentView deploymentView = workspace.getViews().createDeploymentView(generateViewKey(workspace, "Deployment"), "A deployment view for " + deploymentEnvironment);
                deploymentView.setEnvironment(deploymentEnvironment);
                deploymentView.addAllDeploymentNodes();
                deploymentView.enableAutomaticLayout(AutomaticLayout.RankDirection.TopBottom, 300, 300);
            }
        }

        Styles styles = workspace.getViews().getConfiguration().getStyles();
        if (styles.getElements().isEmpty() && styles.getRelationships().isEmpty() && workspace.getViews().getConfiguration().getThemes() == null) {
            System.out.println(" - no styles or themes defined; creating default styles");
            styles.addElementStyle(Tags.ELEMENT).shape(Shape.RoundedBox);
            styles.addElementStyle(Tags.SOFTWARE_SYSTEM).background("#1168bd").color("#ffffff");
            styles.addElementStyle(Tags.CONTAINER).background("#438dd5").color("#ffffff");
            styles.addElementStyle(Tags.COMPONENT).background("#85bbf0").color("#000000");
            styles.addElementStyle(Tags.PERSON).background("#08427b").color("#ffffff").shape(Shape.Person);
        }
    }

    private String generateViewKey(Workspace workspace, String type) {
        DecimalFormat format = new DecimalFormat("000");
        return format.format(workspace.getViews().getViews().size() + 1) + "-" + type;
    }

}