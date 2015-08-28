#!/bin/bash

OLD_PWD=$PWD
cd ${0%/*}
DIR="./datasets"
TEMP_DIR="/tmp"
CREATION_TIMESTAMP="$(date +%s%N)"
FILENAME="raw_questions_$CREATION_TIMESTAMP.csv"
TEMP_FILE="/tmp/raw_questions.csv"
FILE="$DIR/$FILENAME"

echo
echo "Preparing dataset folder . . ."

mkdir -p $DIR

echo
echo "Creating csv raw dataset in temp folder . . ."

mysql -D stackoverflow_march < query_raw_questions.sql #-e "set @temp_file='$TEMP_FILE'; source query_raw_questions.sql;" #TODO: log time

echo
echo "Moving dataset to file $FILE . . ."

mv $TEMP_FILE $FILE #TODO: log time

echo
echo "CSV dataset successfully exported!"

cd $OLD_PWD
