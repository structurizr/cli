#!/usr/bin/env bash

set -o errexit -o noclobber -o nounset -o pipefail

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
java -cp "$SCRIPT_DIR:$SCRIPT_DIR"/lib/* com.structurizr.cli.StructurizrCliApplication "$@"
