Usage:
--

Given all the raw accelerometer readings for 40 sessions of 25 letters each,
do the following steps to extract the .arff features file for feeding to the
 learner.

<h4>Breaking sessions into letter groups</h4>
```$ cd code/scripts```  
```$ paragraph-to-arff-features.sh raw-dataset```

Gives the grouped letter files in ```data/all-letters/```
<h4>Extract non-FFT features</h4>

```$ cd code/scripts```  
```$ get-nonfft-features.sh all-letters lr```

**lr** is for getting the L/D labeled features. Use **ud** for U/D and **triad**
for Triad labeling. Gives the non-FFT labeled features
in ```data/features/all-letters.csv```

<h4> Generating the FFT features</h4>
```$ cd code/feature_extractor```  
```$ Rscript SpectogramFeatureWriter.R ./../../data/all-letters-gforce```

Generates the FFT features for each letter in ```data/features/fft_*.csv```

<h4> Combining all features together </h4>
```$ cd code/scripts```  
```$ combine_features.sh all-letters.csv```

Generates the all the labeled features in the file ```data/features/all-letters_combined.csv```

<h4> Prepare training file </h4>
```$ cd data/features```  
```$ cp all-letters_combined.csv all-letters_features.arff```  
If the labels are L/R labels, remove the first line from
```all-letters_features.arff``` and paste the template
```code/scripts/template_lr.arff``` as head of the file. Do the same steps
for the corresponding labels as needed.

<h4>Output</h4>
The file ```data/features/all-letters_features.arff``` can be fed to the
classifier for learning the model.
