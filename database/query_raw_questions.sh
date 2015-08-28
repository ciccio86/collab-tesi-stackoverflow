#!/bin/bash

OLD_PWD=$PWD
cd ${0%/*}
DIR="./datasets"
TEMP_DIR="/tmp"
LOG_DIR="./logs"
CREATION_TIMESTAMP="$(date +%s)"
FILENAME="raw_questions_$CREATION_TIMESTAMP.csv"
TEMP_FILE="/tmp/raw_questions.csv"
FILE="$DIR/$FILENAME"
LOGFILE="$LOG_DIR/log_$CREATION_TIMESTAMP.log"
ERRORFILE="$LOG_DIR/errors_$CREATION_TIMESTAMP.log"

echo
echo "Preparing dataset and logs folder . . ."

mkdir -p $DIR
mkdir -p $LOG_DIR

echo
echo "Removing old temp file . . ."

rm -f /tmp/raw_questions.csv

echo
echo "Creating csv raw dataset in temp folder . . ."

printf "Query execution times.\n======================\n" >> $LOGFILE
{ time mysql -D stackoverflow_march < query_raw_questions.sql 2>> $ERRORFILE ; } 2>> $LOGFILE

echo
echo "Moving dataset to file $FILE . . ."

printf "\nMoving file times.\n==================\n" >> $LOGFILE
{ time mv $TEMP_FILE $FILE 2>> $ERRORFILE ; } 2>> $LOGFILE

#TODO: do not show if any error
echo
echo "CSV dataset successfully exported!"

cd $OLD_PWD
