#!/bin/bash
# Nut start up script
# ----------------------------------------------------------------------------
export NUT_VERSION=1.2
export NUT_HOME=~/github/nutRepository
java -Xmx512M -classpath target/classes:$NUT_HOME/nut/core/Nut-1.2.jar:$NUT_HOME/nut/logging/Log-1.2.jar:$NUT_HOME/org/codehaus/plexus/plexus-utils-3.0.jar:$NUT_HOME/junit/junit-4.10.jar nut.Nut "-Dversion=$NUT_VERSION" "-Dhome=$NUT_HOME" $*
