## Usage
## $ dos2unix combine_features.sh
## $ ./combine_features.sh

path=./../../data/features

# remove old files if they are already present
rm -f $path/all.tmp.csv
rm -f $path/data1.tmp.csv
rm -f $path/all_combined.tmp.csv

# header for the ffts features
head -n 1 $path/fft_a.csv > $path/data1.tmp.csv

# append all the fft files into one
find $path -name "fft*\.csv" -exec sed '1 d' {} \; >> $path/data1.tmp.csv

# remove trailing  character from the file if any
sed -e "s///g" $path/all.csv > $path/all.tmp.csv
#cat $path/all.csv > $path/all.tmp.csv

# line by line concatenation of the files
paste -d"," $path/all.tmp.csv $path/data1.tmp.csv > $path/all_combined.tmp.csv

# remove the temporary files created
rm $path/all.tmp.csv -f
rm $path/data1.tmp.csv -f

# remove the label column
cut $path/all_combined.tmp.csv -d"," -f-7,9- > $path/without_label.tmp.csv

# get the label column
cut $path/all_combined.tmp.csv -d"," -f8 > $path/labels.tmp.csv

# add the label column to the last
paste -d"," $path/without_label.tmp.csv $path/labels.tmp.csv > \
$path/all_combined.csv

# remove tmp files
rm -f $path/all_combined.tmp.csv
rm -f $path/without_label.tmp.csv
rm -f $path/labels.tmp.csv