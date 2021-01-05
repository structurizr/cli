# Getting started

The Structurizr CLI provides tooling to parse [Structurizr DSL](https://github.com/structurizr/dsl) workspace definitions, upload them to the Structurizr cloud service/on-premises installation, and [export diagrams to other formats](export.md) (e.g. PlantUML, Mermaid, and WebSequenceDiagrams).

## 1. Download and install the Structurizr CLI

There are a number of ways to download/install the Structurizr CLI.

### Local installation

Download the Structurizr CLI from the [releases page](https://github.com/structurizr/cli/releases), and unzip. You will need Java (version 8+) installed, and available to use from your command line (__please note that the CLI does not work with Java versions 11.0.0-11.0.3__).

### Homebrew (MacOS only)

The Structurizr CLI can be installed via [Homebrew](https://brew.sh) as follows:

```
brew install structurizr-cli
```

And to upgrade:

```
brew update
brew upgrade structurizr-cli
```

### Scoop (Windows only)

The Structurizr CLI can be installed via [Scoop](https://scoop.sh) as follows:

```
scoop bucket add extras
scoop install structurizr-cli
```

And to upgrade:

```
scoop update structurizr-cli
```

### Docker

The Structurizr CLI can be built into a Docker container. See [structurizr-cli-docker](https://github.com/leopoldodonnell/structurizr-cli-docker) for details.

### Build from source

To build from the source, clone this repo and run the following command:

```
gradlew build
```

## 2. Create a software architecture model with the DSL

Create a new empty file with your favourite text editor, and copy the following text into it.

```
workspace "Getting Started" "This is a model of my software system." {

    model {
        user = person "User" "A user of my software system."
        softwareSystem = softwareSystem "Software System" "My software system."

        user -> softwareSystem "Uses"
    }

    views {
        systemContext softwareSystem "SystemContext" "An example of a System Context diagram." {
            include *
            autoLayout
        }

        styles {
            element "Software System" {
                background #1168bd
                color #ffffff
            }
            element "Person" {
                shape person
                background #08427b
                color #ffffff
            }
        }
    }
    
}
```

Save this file into the unzipped CLI directory.

This DSL definition:

- creates a person named "User"
- creates a software system named "Software System"
- creates a relationship between the person and the software system
- create a System Context view for the software system
- creates some element styles that will be applied when rendering the diagram

Please note that the Structurizr CLI will create some default views and styles for you if you don't define them yourself in the DSL. See [Defaults](docs/defaults.md) for more details.

## 3. Upload to Structurizr

If you've not done so, follow [Structurizr - Getting started](https://structurizr.com/help/getting-started) to sign up for a free Structurizr account, and create a workspace. To upload your workspace to the Structurizr cloud service/on-premises installation, you will need your workspace ID, API key and secret. See [Structurizr - Workspaces](https://structurizr.com/help/workspaces) for information about finding these.

Open a terminal, and issue the following command to upload the workspace (you should change to the unzipped CLI directory, or have it on your path):

```
./structurizr.sh push -id WORKSPACE_ID -key KEY -secret SECRET -workspace WORKSPACE_FILE
```

or

```
structurizr.bat push -id WORKSPACE_ID -key KEY -secret SECRET -workspace WORKSPACE_FILE
```

- WORKSPACE_ID: your workspace ID
- API_KEY: your API key
- API_SECRET: your API secret
- WORKSPACE_FILE: the name of your workspace DSL file

## 4. Open the workspace in Structurizr

You can now sign in to your Structurizr account, and open the workspace from [your dashboard](https://structurizr.com/dashboard). Your workspace should now contain a diagram like this:

![Getting started](images/getting-started.png)