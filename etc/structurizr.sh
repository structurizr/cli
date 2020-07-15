#!/bin/bash
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
java -jar $SCRIPT_DIR/structurizr-cli-1.3.1.jar "$@"
