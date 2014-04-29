#!/bin/bash

if [[ $# != 2 ]]
then
    echo 'usage: ./get-gforce-smoothed.sh <input-dir> <output-dir>';
    exit
fi

path=../../data

# copy the class files to this directory
cp ../bin/*.class .

# clean the directories if they already exist
rm -rf $path/$2
rm -rf $path/$2-gforce

# run the smoothing script on the dataset
python ../preprocessor/exp_filter.py $path/$1 $path/$2

# run the feature extractor to extract the gforce values
java -cp . FeatureExtractor $2 gforce
rm -f $path/features/$2.csv
echo "cleaning the features file $path/features/$2.csv"

# clean the class files
rm -f *.class
echo "cleaning .class files"

# copy the gforce files to a separate folder
mkdir $path/$2-gforce
echo "new directory created '$path/$2-gforce'"
cp -r $path/$2/* $path/$2-gforce/
find $path/$2 -name "*gforce\.csv" -exec rm -f {} +
find $path/$2-gforce ! -name "*gforce\.csv" -type f -exec rm -f {} +