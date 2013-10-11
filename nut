#!/bin/bash
# Nut start up script
# ----------------------------------------------------------------------------
export NUT_VERSION=2.0
export NUT_HOME=~/github/nutRepository
java -Xmx512M -classpath target/classes:$NUT_HOME/nut/Nut-2.0.jar:$NUT_HOME/org/codehaus/plexus/plexus-utils-3.0.jar:$NUT_HOME/junit/junit-4.10.jar nut.Nut "-Dversion=$NUT_VERSION" "-Dhome=$NUT_HOME" $*
