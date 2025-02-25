#!/bin/zsh

# - this script merges the contents of the structurizr/ui repository into this directory,
# - this has only been tested on MacOS

export STRUCTURIZR_BUILD_NUMBER=$1
export STRUCTURIZR_UI_DIR=../structurizr-ui
export STRUCTURIZR_CLI_DIR=.

rm -rf $STRUCTURIZR_CLI_DIR/src/main/resources/static
mkdir -p $STRUCTURIZR_CLI_DIR/src/main/resources/static

# JavaScript
mkdir -p $STRUCTURIZR_CLI_DIR/src/main/resources/static/js
cp -a $STRUCTURIZR_UI_DIR/src/js/jquery-3.6.3.min.js $STRUCTURIZR_CLI_DIR/src/main/resources/static/js
cp -a $STRUCTURIZR_UI_DIR/src/js/bootstrap-3.3.7.min.js $STRUCTURIZR_CLI_DIR/src/main/resources/static/js
cp -a $STRUCTURIZR_UI_DIR/src/js/lodash-4.17.21.js $STRUCTURIZR_CLI_DIR/src/main/resources/static/js
cp -a $STRUCTURIZR_UI_DIR/src/js/backbone-1.4.1.js $STRUCTURIZR_CLI_DIR/src/main/resources/static/js
cp -a $STRUCTURIZR_UI_DIR/src/js/joint-3.6.5.js $STRUCTURIZR_CLI_DIR/src/main/resources/static/js
cp -a $STRUCTURIZR_UI_DIR/src/js/structurizr.js $STRUCTURIZR_CLI_DIR/src/main/resources/static/js
cp -a $STRUCTURIZR_UI_DIR/src/js/structurizr-util.js $STRUCTURIZR_CLI_DIR/src/main/resources/static/js
cp -a $STRUCTURIZR_UI_DIR/src/js/structurizr-ui.js $STRUCTURIZR_CLI_DIR/src/main/resources/static/js
cp -a $STRUCTURIZR_UI_DIR/src/js/structurizr-workspace.js $STRUCTURIZR_CLI_DIR/src/main/resources/static/js
cp -a $STRUCTURIZR_UI_DIR/src/js/structurizr-diagram.js $STRUCTURIZR_CLI_DIR/src/main/resources/static/js
cp -a $STRUCTURIZR_UI_DIR/src/js/structurizr-quick-navigation.js $STRUCTURIZR_CLI_DIR/src/main/resources/static/js
cp -a $STRUCTURIZR_UI_DIR/src/js/structurizr-navigation.js $STRUCTURIZR_CLI_DIR/src/main/resources/static/js
cp -a $STRUCTURIZR_UI_DIR/src/js/structurizr-tooltip.js $STRUCTURIZR_CLI_DIR/src/main/resources/static/js
cp -a $STRUCTURIZR_UI_DIR/src/js/structurizr-embed.js $STRUCTURIZR_CLI_DIR/src/main/resources/static/js

# CSS
mkdir -p $STRUCTURIZR_CLI_DIR/src/main/resources/static/css
cp -a $STRUCTURIZR_UI_DIR/src/css/bootstrap-3.3.7.min.css $STRUCTURIZR_CLI_DIR/src/main/resources/static/css
cp -a $STRUCTURIZR_UI_DIR/src/css/joint-3.6.5.css $STRUCTURIZR_CLI_DIR/src/main/resources/static/css
cp -a $STRUCTURIZR_UI_DIR/src/css/structurizr-diagram.css $STRUCTURIZR_CLI_DIR/src/main/resources/static/css
cp -a $STRUCTURIZR_UI_DIR/src/css/structurizr-static.css $STRUCTURIZR_CLI_DIR/src/main/resources/static/css
cp -a $STRUCTURIZR_UI_DIR/src/css/structurizr-static-dark.css $STRUCTURIZR_CLI_DIR/src/main/resources/static/css

# HTML
cp -a $STRUCTURIZR_UI_DIR/src/static.html $STRUCTURIZR_CLI_DIR/src/main/resources/static/index.html

cd src/main/resources/static
rm ../static.zip
zip -r ../static.zip .
cd ../../../..
rm -rf $STRUCTURIZR_CLI_DIR/src/main/resources/static

