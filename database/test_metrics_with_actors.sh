#!/bin/bash

OLD_PWD=$PWD
cd ${0%/*}
LOG_DIR="$PWD/logs"
CREATION_TIMESTAMP="$(date +%s)"
LOGFILE="$LOG_DIR/metrics_with_actors_log_$CREATION_TIMESTAMP.log"
RAW_QUESTIONS_FILE=$1
OUTPUT_FILE=$2
NUMBER_OF_ACTORS=( 1 2 4 8 16 32 64 128 256 512 )

#change into the directory of the sbt project
cd ../stackexchange-metrics-calculator

echo
echo "Preparing dataset and logs folder . . ."

mkdir -p $LOG_DIR

echo "questions;workers;executionTimeinSeconds" > $LOGFILE


for i in "${NUMBER_OF_ACTORS[@]}"
do

echo
echo "Testing with $i actors . . ."

sbt --error 'set showSuccess := false' "runMain it.uniba.di.collab.stackexchange.actorsystem.Main $1 $2 $i" >> $LOGFILE

done

echo "Finished: view log at $LOGFILE"

cd $OLD_PWD