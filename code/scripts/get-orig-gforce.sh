# copy the class files to this directory
cp ../bin/*.class .

# run the feature extractor to extract the gforce values
java -cp . FeatureExtractor new-data gforce
rm -f ../../data/features/new-data.csv
echo "cleaning the features file ../../data/features/new-data.csv"

# clean the class files
rm -f *.class
echo "cleaning .class files"

# copy the gforce files to a separate folder
mkdir ../../data/new-data-gforce
echo "new directory created '../../data/new-data-gforce'"
cp -r ../../data/new-data/* ./../../data/new-data-gforce/
find ../../data/new-data -name "*gforce\.csv" -exec rm -f {} +
find ../../data/new-data-gforce ! -name "*gforce\.csv" -type f -exec rm -f {} +