## Usage
## $ dos2unix combine_features.sh
## $ ./combine_features.sh

# remove old files if they are already present
rm -f all.tmp.csv
rm -f data1.tmp.csv
rm -f all_combined.tmp.csv

# header for the ffts features
head -n 1 fft_a.csv > data1.tmp.csv

# append all the fft files into one
find . -name "fft*\.csv" -exec sed '1 d' {} \; >> data1.tmp.csv

# remove trailing  character from the file if any
sed -e "s///" all.csv > all.tmp.csv

# line by line concatenation of the files
paste -d"," all.tmp.csv data1.tmp.csv > all_combined.tmp.csv

# remove the temporary files created
rm all.tmp.csv -f
rm data1.tmp.csv -f

# remove the label column
cut all_combined.tmp.csv -d"," -f-7,9- > without_label.tmp.csv

# get the label column
cut all_combined.tmp.csv -d"," -f8 > labels.tmp.csv

# add the label column to the last
paste -d"," without_label.tmp.csv labels.tmp.csv > all_combined.csv

# remove tmp files
rm -f all_combined.tmp.csv
rm -f without_label.tmp.csv
rm -f labels.tmp.csv
