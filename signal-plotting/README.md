Usage
-----

To compute the **time(in us)** vs **g-force** plot, we need the values
of the g-force to be comptuted from the x-, y- and z- direction components
of the acceleration caused due to a motion. One can use *GForceComputation*
to achieve this as follows by specifying the directory for which you wish
to compute the table for all the .csv files present in it.

####Example

```
$ java GForceComputation ./../letters/
```

The output time vs. g-force table in generated in the same directory with
the .data extension.