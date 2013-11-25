#!/bin/bash
# Nut start up script
# ----------------------------------------------------------------------------
export NUT_VERSION=1.1
export NUT_HOME=~/github/nutRepository
java -Xmx512M -classpath $NUT_HOME/nut/core/Nut-1.1.jar:$NUT_HOME/nut/logging/Log-1.1.jar:$NUT_HOME/org/codehaus/plexus/plexus-utils-3.0.jar:$NUT_HOME/com/beust/jcommander-1.7.jar:$NUT_HOME/org/testng/testng-6.8.7.jar nut.Nut "-Dversion=$NUT_VERSION" "-Dhome=$NUT_HOME" $*
