#!/bin/bash

if [[ $# -lt 1 ]]
then
    echo 'usage: ./get-nonfft-features.sh <input-dir> <label-type>';
    echo 'example1: ./get-nonfft-features.sh raw-dataset';
    echo 'example2: ./get-nonfft-features.sh raw-dataset lr';
    echo 'example3: ./get-nonfft-features.sh raw-dataset triad';
    exit
fi

path=../../data
outdir=__$1

# copy the class files to this directory
cp ../feature_extractor/*.java .
javac *.java

# clean the directories if they already exist
rm -rf $path/$outdir

# run the ends clipping script on the dataset
python ../preprocessor/ends_clipper.py $path/$1 $path/$outdir

# run the feature extractor to extract the gforce values
java -cp . FeatureExtractor -d $outdir -gforce -label $2
mv $path/features/$outdir.csv $path/features/$1.csv
echo "features file written at ... $path/features/$1.csv"

# clean the class files
rm -f *.class *.java
echo "cleaning .class files"

# copy the gforce files to a separate folder
rm -rf $path/$1-gforce
mkdir $path/$1-gforce
echo "new directory created '$path/$1-gforce'"
cp -r $path/$outdir/* $path/$1-gforce/
find $path/$outdir -name "*gforce\.csv" -exec rm -f {} +
find $path/$1-gforce ! -name "*gforce\.csv" -type f -exec rm -f {} +

# clean tmp files
rm -rf $path/$outdir