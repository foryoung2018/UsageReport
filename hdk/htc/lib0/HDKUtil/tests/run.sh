#!/bin/bash

pwd
android update lib-project --target 1 -p ./..
ant clean -buildfile ../build.xml
if [ $? == 0 ]
then
    ant debug -buildfile ../build.xml
if [ $? == 0 ]
then
    ant coverage
fi
fi
