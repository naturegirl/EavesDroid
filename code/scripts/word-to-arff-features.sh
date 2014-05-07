#!/bin/bash

# reads the directory of word signal files and processes them
# to generate the features in .arff format

if [[ $# != 1 ]]
then
    echo 'usage: ./word-to-arff-features.sh <input-dir>';
    echo 'example: ./word-to-arff-features.sh input-words';
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
cp ../feature_extractor/*.class .
echo "classes: files copied to this directory"

# generate the partial feature files
java -cp . WordFeatureExtractor __data
mv $path/__data.feature $path/__data
mv $path/__data/__data.feature $path/__data/partial-features
mv $path/__data.letters.gforces $path/__data
printf "partial-features: generated with gforce values\n\n"

# generate the fft features for the letters of the words
mkdir $path/__data/fft_features
Rscript ../feature_extractor/SpectogramFeatureWriter.R \
$path/__data/__data.letters.gforces $path/__data/fft_features
printf "fft: features generated\n\n"

# combine the partial and fft features to generate the combined features files
mkdir $path/__data/combined-features
cd $path/__data/partial-features
find . -name "*.csv" | cut -c 3- | while read file; do \
paste -d"," "$file" "../fft_features/fft_$file" > ../combined-features/"$file";\
done
printf "combined: features into $path/__data/combined-features\n\n"

# add the template for the arff file and convert to arff file
cd ..
mkdir $1.arff
cd combined-features
find . -name "*.csv" | cut -c 3- | while read file; do \
rm -rf "$file".arff
cat ../../../code/scripts/template.arff > "../$1.arff/$file".arff
sed '1 d' "$file" >> "../$1.arff/$file".arff
done
printf "arff: files generated from the feature files\n\n"

# move the arff files outside
cd ..
rm -rf ../$1.arff
mv $1.arff ../
cd ../../code/scripts
printf "features: files present in $path/$1.arff\n\n"

# draw indiv letter signals for the words
Rscript ../../signal-plotting/R/draw-processed-signals.R \
$path/__data/__data.letters.gforces
printf "draw: signals for the individual letters\n\n"

# clean the files
#rm -rf $path/__data
rm -f *.class
echo "cleaning files"
