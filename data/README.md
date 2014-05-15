data
--
## raw-dataset
Raw accelerometer data collected using 25 random alphabet
keystrokes in 40 sessions.  
**Format**: < timestamp, x, y, z >  

## all-letters
Cleaned up raw-accelerometer readings for the different alphabets grouped together.

## dictionary
[Harvard sentences](http://www.cs.columbia.edu/~hgs/audio/harvard.html)
used in making predictions and in testing

## features
mean, skewness, kurtosis, min, max, rms, variance, fft-coefficients

## dataset
Training files labeled with L/R and U/D labels. ```testing.csv``` used in
automated construction of signals for test words.

## nytimes-data
Used to test on a real-world data set [details in
[paper](https://github.com/naturegirl/EavesDroid/blob/master/paper/report.pdf)].
