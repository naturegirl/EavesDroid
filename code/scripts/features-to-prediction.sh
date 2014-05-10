#!/bin/bash

# read the features file from the argument directory
# and make the predictions of the top K words

if [[ $# != 3 ]]
then
    echo 'usage: ./features-to-prediction.sh <features-dir> <num-dicts> <num-matches';
    echo 'example: ./features-to-prediction.sh input-words.arff 3 5';
    exit
fi

path=../../data

# create copy of all the .arff files into a new dir
rm -rf $path/__pred
mkdir $path/__pred
mkdir $path/__pred/features
cp -r $path/$1/* $path/__pred/features
echo "copy: all *.arff copied to $path/__pred"

# copy the class files to this directory
javac ../dictionary_MLW/*.java
mv ../dictionary_MLW/*.class .
echo "classes: files moved to this directory"

# generate the predictions
cp ../boosting/*.rda .
cp ../boosting/lr-ud-predictor.R .
find $path/__pred/features -name "*arff" | while read file; do
Rscript lr-ud-predictor.R $file
java -cp . LRUDpredictor $file.pred $2 $3
printf "\n"
done
echo "word predictions: done"

# clean the files
#rm -rf $path/__data
rm -f *.rda
rm -f *.class
rm -f lr-ud-predictor.R
echo "cleaning files"
