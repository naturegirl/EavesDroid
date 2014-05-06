cos424project
=============

## Steps

1. Clean signal files, removing head and tail. 

2. Collect remaining features: ffts, mfccs

3. Breaking down of a recorded word signal into characters

4. L/R predictor (i.e. neural network)

5. Clustering on training set (distance, normalization?)

6. Clustering on test set

7. Dictionary, most likely word (Clustering returns top 5 characters)


###'g-force' computation

When a key is pressed, the accelerometer reads the x-, y- and z- direction
components of the acceleration of the vibration received. To get the
acceleration relative to the gravity, one needs to compute the following
**g-force** = ![equation](https://raw.githubusercontent.com/naturegirl/cos424project/82a2afa50d656e9fd618c652bf3dbb320eb42120/data/g-force.jpg)

Here *g* = 9.81 m/s<sup>2</sup> is the acceleration due to gravity.

### Triads assignment

- q a w: 1
- z s x: 2
- e d r: 3
- f c v: 4
- t g y: 5
- h b n: 6
- u j i: 7
- o l p: 8
- k m: 9













