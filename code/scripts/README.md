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
```$ get-nonfft-features.sh all-letters```  
Gives the non-FFT **default** labeled features
in ```data/features/all-letters.csv```

<h4> Generating the FFT features</h4>
```$ cd code/feature_extractor```  
```$ Rscript SpectogramFeatureWriter.R ./../../data/all-letters-gforce```  
Generates the FFT features for each letter in ```data/features/fft_*.csv```

<h4> Combining all features together </h4>
```$ cd code/scripts```  
```$ combine_features.sh all-letters.csv```  
Generates the all the **default** labeled features in the file
```data/features/all-letters-labeled.csv```