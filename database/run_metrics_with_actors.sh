#!/bin/bash

OLD_PWD=$PWD
cd ${0%/*}
LOG_DIR="$PWD/logs"
CREATION_TIMESTAMP="$(date +%s)"
RAW_QUESTIONS_FILE=$1
OUTPUT_FILE=$2
NUMBER_OF_ACTORS=$3
LOGFILE="$LOG_DIR/metrics_with_$NUMBER_OF_ACTORS_actors_log_$CREATION_TIMESTAMP.log"

#change into the directory of the sbt project
cd ../stackexchange-metrics-calculator

echo
echo "Preparing dataset and logs folder . . ."

mkdir -p $LOG_DIR

echo "questions;workers;executionTimeinSeconds" > $LOGFILE

echo
echo "Running with $NUMBER_OF_ACTORS actors . . ."

sbt --error 'set showSuccess := false' "runMain it.uniba.di.collab.stackexchange.actorsystem.Main $RAW_QUESTIONS_FILE $OUTPUT_FILE $NUMBER_OF_ACTORS" >> $LOGFILE

echo "Finished: view log at $LOGFILE"

cd $OLD_PWD