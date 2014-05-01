## Usage
## $ dos2unix combine_features.sh
## $ ./combine_features.sh

path=./../../data/features

if [[ $# != 1 ]]
then
    echo 'usage: ./combine_features.sh <input-partial-csv>';
    exit
fi

# header for the ffts features
find $path -name "fft_*.csv" | head -n 1 | xargs -I {} head -n 1 {} \
> $path/$1.data1.tmp.csv
echo "header of the ffts extracted"

# append all the fft files into one
find $path -name "fft*\.csv" -exec sed '1 d' {} \; >> $path/$1.data1.tmp.csv
echo "fft-files combined into one file"

# remove trailing  character from the file if any
sed -e "s///g" $path/$1.csv > $path/$1.tmp.csv
echo "removing the ^M character"

# line by line concatenation of the files
paste -d"," $path/$1.tmp.csv $path/$1.data1.tmp.csv > $path/$1_combined.tmp1.csv
echo "data file and the ffts combined"

# remove trailing  character from the file if any
sed -e "s///g" $path/$1_combined.tmp1.csv > $path/$1_combined.tmp.csv
echo "removing the ^M character"

# remove the temporary files created
rm $path/$1.tmp.csv -f
rm $path/$1.data1.tmp.csv -f
rm $path/$1_combined.tmp1.csv -f
echo "removing tmp files"

# remove the label column
cut $path/$1_combined.tmp.csv -d"," -f-7,9- > $path/without_label.tmp.csv

# get the label column
cut $path/$1_combined.tmp.csv -d"," -f8 > $path/labels.tmp.csv

# add the label column to the last
paste -d"," $path/without_label.tmp.csv $path/labels.tmp.csv > \
$path/$1_combined.csv
echo "label column realigned"

# remove tmp files
rm -f $path/$1_combined.tmp.csv
rm -f $path/without_label.tmp.csv
rm -f $path/labels.tmp.csv

echo "combined features file written to.... $path/$1_combined.csv"