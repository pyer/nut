#!/bin/bash
# Nut start up script
# ----------------------------------------------------------------------------
[ -f "target/test-classes" ] || mkdir -p target/test-classes
export NUT_VERSION=1.1
export NUT_HOME=~/github/nutRepository
java -Xmx512M -classpath target/classes:target/test-classes:$NUT_HOME/nut/core/Nut-1.1.jar:$NUT_HOME/nut/logging/Log-1.1.jar:$NUT_HOME/org/codehaus/plexus/plexus-utils-3.0.jar:$NUT_HOME/junit/junit-4.10.jar nut.Nut "-Dversion=$NUT_VERSION" "-Dhome=$NUT_HOME" $*
