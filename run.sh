#!/usr/bin/env bash

. env.sh

JAR_PATH="target/mbi-project-1.0-SNAPSHOT.jar"
MAIN_CLASS="pl.edu.pw.elka.mbi.genomecoverage.App"
MASTER="local[*]"

#mvn package
"$SPARK_HOME/bin/spark-submit" --class "$MAIN_CLASS" --master "$MASTER" "$JAR_PATH"
