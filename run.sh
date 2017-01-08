#!/usr/bin/env bash

. env.sh

JAR_PATH="target/mbi-project-1.0-SNAPSHOT.jar"
MASTER="local[*]"

#mvn package
"$SPARK_HOME/bin/spark-submit" --class "SimpleApp" --master "$MASTER" "$JAR_PATH"
