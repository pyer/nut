@REM ----------------------------------------------------------------------------
@REM Nut start up script
@REM ----------------------------------------------------------------------------
@echo off
set NUT_VERSION=1.2-SNAPSHOT
set NUT_HOME=C:\ab\nut\Nut\src\resources
set NUT_REPOSITORY=C:\.nut
set NUT_ERROR=0
rem java.exe -Xmx177M -classpath C:\ab\nut\Nut\target\Nut.jar ab.nut.Nut "-Dversion=%NUT_VERSION%" "-Dhome=%NUT_HOME%" "-Drepository=%NUT_REPOSITORY%" %*
java.exe -Xmx177M -classpath C:\.nut\ab\nut\Nut\%NUT_VERSION%\Nut-%NUT_VERSION%.jar;C:\.nut\junit\junit\4.4\junit-4.4.jar;target/test-classes ab.nut.Nut "-Dversion=%NUT_VERSION%" "-Dhome=%NUT_HOME%" "-Drepository=%NUT_REPOSITORY%" %*
if ERRORLEVEL 1 set NUT_ERROR=1
exit /B %NUT_ERROR%
