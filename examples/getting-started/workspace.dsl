workspace "Getting Started" "This is a model of my software system." {

    model {
        user = person "User" "A user of my software system."
        mySystem = softwareSystem "Software System" "My software system."

        user -> mySystem "Uses"
    }

    views {
        systemContext mySystem "SystemContext" "An example of a System Context diagram." {
            include *
            autoLayout
        }

        styles {
            element "Software System" {
                background #1168bd
                color #ffffff
            }
            element "Person" {
                shape Person
                background #08427b
                color #ffffff
            }
        }
    }
    
}
