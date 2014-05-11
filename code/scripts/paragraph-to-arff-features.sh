#!/bin/bash

# reads the directory of word signal files and processes them
# to generate the features in .arff format

if [[ $# != 1 ]]
then
    echo 'usage: ./paragraph-to-arff-features.sh <input-dir>';
    echo 'example: ./paragraph-to-arff-features.sh input-para';
    exit
fi

path=../../data

# create copy of all the .csv files into a new dir
rm -rf $path/__data
mkdir $path/__data
mkdir $path/__data/files
cp -r $path/$1/* $path/__data/files
printf "copy: all *.csv copied to $path/__data\n\n"

# run the start/stop ends removing-script on the dataset
python ../preprocessor/ends_clipper.py $path/__data $path/__data/clipped
rm -rf $path/__data/files
mv $path/__data/clipped/* $path/__data
rm -rf $path/__data/clipped
printf "clipper: start/stop ends clipped\n\n"

# copy the class files to this directory
javac ../feature_extractor/*.java
mv ../feature_extractor/*.class .
echo "classes: files moved to this directory\n\n"

# generate the individual letter files
java -cp . WordFeatureExtractor __data timexyz
mv $path/__data.indivletters $path/__data
mv $path/__data.letters.gforces $path/__data
printf "individual: letters files generated\n\n"

# label the individual letter files
mkdir $path/__data/labels
cp ../labeler/data/* $path/__data/labels/
cd $path/__data/__data.indivletters
numdirs=$(ls -l -d */ | wc -l)
dir=1
rm -rf ../__data.labeled.letters
mkdir ../__data.labeled.letters
while [[ $dir -le $numdirs ]]
do
    mkdir ../__data.labeled.letters/$dir
    i=0
    while read line; do
	cp $dir*/$i*.csv ../__data.labeled.letters/$dir/$line\_$dir.csv
	((i = i + 1))
    done < ../labels/$dir.txt
    ((dir = dir + 1))
done
cd ../../../code/scripts
printf "labelled: letters and separated\n\n"

# move all the letters into one directory
rm -rf $path/__data/all-labelled-letters
mkdir $path/__data/all-labelled-letters
find $path/__data/__data.labeled.letters -name "*csv" |\
xargs -I {} cp {} ../../data/__data/all-labelled-letters
printf "moved: all letters combined into one directory\n\n"

# separate files into letter folders
cd $path/__data
rm -rf all-letters
mkdir all-letters
for x in {a..z}
do
    mkdir all-letters/$x
    mv all-labelled-letters/$x*csv all-letters/$x 2>/dev/null
done
cd ../../code/scripts
printf "separated: letters into folders\n\n"

# move the folder outside
rm -rf $path/all-letters
cp -r $path/__data/all-letters $path
find $path/all-letters -type d -empty -exec rmdir {} \;