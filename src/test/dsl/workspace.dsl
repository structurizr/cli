workspace {

    model {
        softwareSystem "Software System" {
            !docs docs
            !adrs adrs
        }
    }

    views {
        systemLandscape "SystemLandscape" {
            include *
            autolayout
        }

        theme default
    }

}