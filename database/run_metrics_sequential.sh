#!/bin/bash

OLD_PWD=$PWD
cd ${0%/*}
LOG_DIR="$PWD/logs"
CREATION_TIMESTAMP="$(date +%s)"
RAW_QUESTIONS_FILE=$1
OUTPUT_FILE=$2
LOGFILE="$LOG_DIR/sequential_metrics_$CREATION_TIMESTAMP.log"

#change into the directory of the sbt project
cd ../stackexchange-metrics-calculator

echo
echo "Preparing dataset and logs folder . . ."

mkdir -p $LOG_DIR

sbt --error 'set showSuccess := false' "runMain it.uniba.di.collab.stackexchange.scripts.calculateMetricsSequential $RAW_QUESTIONS_FILE $OUTPUT_FILE" >> $LOGFILE

echo "Finished: view log at $LOGFILE"

cd $OLD_PWD