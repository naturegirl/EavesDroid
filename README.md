EavesDroid: Keystroke Recovery using Smartphone Accelerometers
=============

In this [paper](https://github.com/naturegirl/EavesDroid/blob/master/paper/report.pdf),
we demonstrate the usage of a smartphone accelerometer to
eavesdrop on a nearby computer user and recover text based on recorded
keyboard vibrations. We present EavesDroid, a proof-of-concept application
that can be used to record and process accelerometer data and leverage it to
reconstruct the original typed text.
[EavesDroid](https://github.com/naturegirl/EavesDroid/blob/master/paper/report.pdf)
uses AdaBoost with Decision
Stumps and achieves up to 85% accuracy in recovering original words with an
expected error rate of 2 letters per word. We also make our dataset of
accelerometer recordings available to the public, the first such dataset to the
best of our knowledge.

##Workflow
- **Generate Features**: Follow the steps in [```code/scripts/README.md```](https://github.com/naturegirl/EavesDroid/blob/master/code/scripts/README.md) to
generate the *default* labeled features file
```data/features/all-letters-labeled.csv``` for the whole of raw data
- **Generate Training/Test Set**: Divide the features into training (66%) and
testing set (33%) using  
```$ cd code/feature_extractor```  
```$ java GenTrainingTesting```  
Generates the training/testing files at  
 - ```data/dataset/training.lr.arff```,
 - ```data/dataset/training.ud.arff```
 - ```data/dataset/testing.csv```
- **Prediction Model Building**: Run the following .R script to build the
prediction model  
```$ cd code/boosting```  
```$ Rscript adaboost-lr-ud.R```  
It creates the models at the locations  
 - ```code/boosting/adaboost.rf.lr.rda```
 - ```code/boosting/adaboost.rf.ud.rda```
- **Predicting**: In order to get predictions on sample text,
say ```data/dictionary/harvard1.txt```, the following steps need to be followed
 1. **Automated Feature Generation**: Generating features for the simulated
signals of the corresponding words
from the file ```data/dataset/testing.csv```
```$ cd data/tester```  
```$ java GenParagraphArff < data/dictionary/harvard1.txt```  
This generates the *un-labeled* features for all the words in the given text at
the location ```data/paragraph_arff```
 2. **Making Predictions**: Use the following commands to generate the word
predictions for the unseen words  
```$ cd code/scripts```  
```$ features-to-prediction.sh paragraph_arff 72 5 true```  
This makes word predictions using 72 dictionaries and gives all the predicted
words (at ```data/possible-words```) with up to a Hamming distance of 5 from
the label predicted for the given simulated word signal.
 3. **Accuracy**: Accuracy of the predictions can be done using  
```$ cd code/tester```  
```$ java Accuracy```
