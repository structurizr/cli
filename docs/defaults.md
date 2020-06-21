# Defaults

This page describes some of the defaults provided by the Structurizr CLI.

## Views

If you don't define any views yourself, the Structurizr CLI will automatically create some default views for you, based upon the elements defined in the model and the following rules:

- A System Landscape view will be created containing all people and software systems.
- A System Context view will be created for each software system, containing all people and software systems directly connected to the software system in scope.
- A Container view will be created for each software system that has one or more containers. All containers belonging to the software system in scope will be added to the view, along with all people and software systems directly connected to those containers.
- A Component view will be created for each container that has one or more components. All components belonging to the container in scope will be added to the view, along with all people, software systems and containers (belonging to the software system in scope) directly connected to those components.
- A Deployment View will be created for each combination of deployment environment and software system that includes one or more container instances. If a deployment environment doesn't have any container instances, a Deployment View will created for that deployment environment if it has infrastructure nodes.

As an example, the following DSL fragment will generate six views as follows:

- System Landscape view
- System Context view for "Software System"
	- Container view for "Software System"
		- Component view for "Web Application"
	- Deployment View for "Software System", "Development" environment
	- Deployment View for "Software System", "Live" environment

```
workspace {

    model {
        user = person "User"
        softwareSystem "Software System" {
            wa = container "Web Application" {
                hpc = component "HomePageController"
            }
            db = container "Database"
         }

        user -> hpc
        hpc -> db

        deploymentEnvironment "Development" {
            deploymentNode "Developer Laptop" {
                containerInstance wa
                containerInstance db
            }
        }

        deploymentEnvironment "Live" {
            deploymentNode "Amazon Web Services" {
                route53 = infrastructureNode "Route 53"
                deploymentNode "EC2" {
                    wa_live = containerInstance wa
                    containerInstance db
                }
            }
        }

        route53 -> wa_live
    }

}
```

## Styles

If you don't define any styles or themes, the following styles will be added for you.

```
element "Element" {
    shape roundedbox
}
element "Software System" {
    background #1168bd
    color #ffffff
}
element "Container" {
    background #438dd5
    color #ffffff
}
element "Component" {
    background #85bbf0
    color #000000
}
element "Person" {
    background #08427b
    color #ffffff
    shape person
}
element "Infrastructure Node" {
    background #ffffff
}
```
