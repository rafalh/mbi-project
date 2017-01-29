#!/usr/bin/env bash

. env.sh

JAR_PATH="target/genome-coverage-1.0-SNAPSHOT-jar-with-dependencies.jar"
MAIN_CLASS="pl.edu.pw.elka.mbi.genomecoverage.App"
MASTER="local[*]"

#mvn package -DskipTests=true
"$SPARK_HOME/bin/spark-submit" --class "$MAIN_CLASS" --master "$MASTER" "$JAR_PATH" $@
