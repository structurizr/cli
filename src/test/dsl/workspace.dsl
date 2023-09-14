workspace {

    model {
        ss = softwareSystem "Software System" {
            !docs docs
            !adrs adrs
        }

        p = person "Person" {

        }

        p -> ss "Calls"

    }

    views {
        systemLandscape "SystemLandscape" {
            include *
            autolayout
        }

        theme default
    }

}
