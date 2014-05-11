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
rm -rf $path/__para
mkdir $path/__para
mkdir $path/__para/files
cp -r $path/$1/* $path/__para/files
printf "copy: all *.csv copied to $path/__para\n\n"

# run the start/stop ends removing-script on the dataset
python ../preprocessor/ends_clipper.py $path/__para $path/__para/clipped
rm -rf $path/__para/files
mv $path/__para/clipped/* $path/__para
rm -rf $path/__para/clipped
printf "clipper: start/stop ends clipped\n\n"

# copy the class files to this directory
javac ../feature_extractor/*.java
mv ../feature_extractor/*.class .
echo "classes: files moved to this directory\n\n"

# generate the individual letter files
java -cp . WordFeatureExtractor __para timexyz
mv $path/__para.indivletters $path/__para
mv $path/__para.letters.gforces $path/__para
printf "individual: letters files generated\n\n"

# label the individual letter files
mkdir $path/__para/labels
cp ../labeler/data/* $path/__para/labels/
cd $path/__para/__para.indivletters
numdirs=$(ls -l -d */ | wc -l)
dir=1
rm -rf ../__para.labeled.letters
mkdir ../__para.labeled.letters
name=0
while [[ $dir -le $numdirs ]]
do
    mkdir ../__para.labeled.letters/$dir
    i=0
    while read line; do
	cp $dir\_*/$i.letter.csv ../__para.labeled.letters/$dir/$line\_$name.csv
	echo "copied $dir\_*/$i.letter.csv to ../__para.labeled.letters/$dir/$line\_$name.csv"
	((i = i + 1))
	((name = name + 1))
	#sleep .1
    done < ../labels/$dir.txt
    ((dir = dir + 1))
done
cd ../../../code/scripts
printf "labelled: letters and separated\n\n"

# move all the letters into one directory
rm -rf $path/__para/all-labelled-letters
mkdir $path/__para/all-labelled-letters
find $path/__para/__para.labeled.letters -name "*csv" |\
xargs -I {} cp {} ../../data/__para/all-labelled-letters
printf "moved: all letters combined into one directory\n\n"

# separate files into letter folders
cd $path/__para
rm -rf all-letters
mkdir all-letters
for x in {a..z}
do
    mkdir all-letters/$x
    cp all-labelled-letters/$x*csv all-letters/$x 2>/dev/null
done
cd ../../code/scripts
printf "separated: letters into folders\n\n"

# move the folder outside
rm -rf $path/all-letters
cp -r $path/__para/all-letters $path
find $path/all-letters -type d -empty -exec rmdir {} \;