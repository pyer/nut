#!/bin/bash
# Nut start up script
# ----------------------------------------------------------------------------
export NUT_VERSION=1.0
export NUT_HOME=~/github/nutRepository
#java -Xmx177M -classpath ./nut/Nut.jar;.nut\junit\junit\4.4\junit-4.4.jar;target/test-classes ab.nut.Nut "-Dversion=%NUT_VERSION%" "-Dhome=%NUT_HOME%" "-Drepository=%NUT_REPOSITORY%" %*
java -Xmx512M -classpath $NUT_HOME/nut/core/Nut-1.0.jar:$NUT_HOME/nut/logging/Log-1.0.jar:$NUT_HOME/junit/junit-4.10.jar:target/test-classes nut.Nut "-Dversion=$NUT_VERSION" "-Dhome=$NUT_HOME" $*
