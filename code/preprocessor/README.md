Usage
---

$ python ends-clipper.py < input-dir > < output-dir >

Given a directory with all the raw accelerometer readings,
it clips the 'start' and 'ending' noise by taking offset
of 150ms at either ends.
