#!/bin/bash

if [[ $# != 1 ]]
then
    echo 'usage: ./get-gforce-smoothed.sh <input-dir>';
    exit
fi

path=../../data

# copy the class files to this directory
cp ../feature_extractor/*.java .
javac *.java

# clean the directories if they already exist
rm -rf $path/$1-gforce

# run the feature extractor to extract the gforce values
java -cp . FeatureExtractor $1 gforce
echo "features file written at ... $path/features/$1.csv"

# clean the class files
rm -f *.class *.java
echo "cleaning .class files"

# copy the gforce files to a separate folder
mkdir $path/$1-gforce
echo "new directory created '$path/$1-gforce'"
cp -r $path/$1/* $path/$1-gforce/
find $path/$1 -name "*gforce\.csv" -exec rm -f {} +
find $path/$1-gforce ! -name "*gforce\.csv" -type f -exec rm -f {} +