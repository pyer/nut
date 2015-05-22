#!/bin/bash
# Nut start up script
# ----------------------------------------------------------------------------
export NUT_VERSION=2.0-SNAPSHOT
export NUT_HOME=/home/pba/nutRepository
java -Xmx512M -classpath ./target/test-classes:$NUT_HOME/nut/Nut-$NUT_VERSION.jar:$NUT_HOME/org/codehaus/plexus/plexus-utils-3.0.jar:$NUT_HOME/com/beust/jcommander-1.7.jar:$NUT_HOME/org/testng/testng-6.8.7.jar nut.Nut "-Dversion=$NUT_VERSION" "-Dhome=$NUT_HOME" $*
