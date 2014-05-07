#!/bin/bash

if [[ $# != 2 ]]
then
    echo 'usage: ./get-stripped-word-letters.sh <input-dir> <output-dir>';
    exit
fi

path=../../data

# copy the class files to this directory
cp ../bin/*.class .

# clean the directories if they already exist
rm -rf $path/$2.cleaned

# run the smoothing script on the dataset
python ../preprocessor/exp_filter.py $path/$1 $path/$2

# generate the partial feature files
java -cp . WordFeatureExtractor $2

# copy the generated feature files to the ../../data/features/ path
cp $path/$2.feature/*.csv $path/features/

echo "copied feature files to $path/features/"