#!/usr/bin/env bash

set -o errexit -o noclobber -o nounset -o pipefail

# Begin with BASH_SOURCE[0]
SCRIPT_PATH="${BASH_SOURCE[0]}"
SCRIPT_DIR="$(cd -P "$( dirname "$SCRIPT_PATH" )" >/dev/null 2>&1 && pwd)"

# Recursively resolve symlinks
while [[ -L "$SCRIPT_PATH" ]]; do
  # Resolve the symlink to it's target path.
  SCRIPT_PATH=$(readlink "$SCRIPT_PATH")
  # If the target path is relative, prepend the symlink's source dir.
  [[ "$SCRIPT_PATH" == /* ]] || SCRIPT_PATH="$SCRIPT_DIR/$SCRIPT_PATH"
  # Update the script dir, resolving dir symlinks in the process.
  SCRIPT_DIR="$(cd -P "$( dirname "$SCRIPT_PATH" )" >/dev/null 2>&1 && pwd)"
done

java -cp "$SCRIPT_DIR:$SCRIPT_DIR"/lib/* com.structurizr.cli.StructurizrCliApplication "$@"
