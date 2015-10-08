#!/bin/bash

OLD_PWD=$PWD
cd ${0%/*}
LOG_DIR="$PWD/logs"
CREATION_TIMESTAMP="$(date +%s)"
VALID_QUESTIONS_FILE=$1
RAW_QUESTIONS_FILE=$2
OUTPUT_FILE=$3
LOGFILE="$LOG_DIR/filter_valid_questions_$CREATION_TIMESTAMP.log"

#change into the directory of the sbt project
cd ../stackexchange-metrics-calculator

echo
echo "Preparing dataset and logs folder . . ."

mkdir -p $LOG_DIR

sbt --error 'set showSuccess := false' "runMain it.uniba.di.collab.stackexchange.scripts.filterQuestionIds $VALID_QUESTIONS_FILE $RAW_QUESTIONS_FILE $OUTPUT_FILE" >> $LOGFILE

echo "Finished: view log at $LOGFILE"

cd $OLD_PWD