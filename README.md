# Sudoku Solver

This is a Scala implementation of a Sudoku solver that uses a local
search hill climb to find a solution. It will choose a
better fitness cost with a probability of .995

Objective function:
Number of repeated numbers in all the rows plus number of repeated
numbers in each quadrant. All columns get intialized to already satisfy 
the property of containing all numbers from 1 to 9.

Transformation function:
Implemented by doing a swap of numbers in the same column yet in
distinct rows. A random column number is picked, then two random rows
are selected with the only requirement that they must be different.

Stopping Criteria:
If the iterations exceed 5 million the the Hill Climb will terminate
even though a solution may not have been found on the given input for the problem.

## Requirements

> java 1.6 or higher

> [sbt 0.12.2](http://www.scala-sbt.org/release/docs/Getting-Started/Setup.html)

## Building

Change into main directory and run:

1. ```sbt reload update```
2. ```sbt compile```
3. ```sbt run <filename>``` where ```<filename>``` is any custom file in
the repository. Look at *.in files for examples for the format

## Testing

Run tests from main directory:

```sbt test```

## Benchmarks

    Below are some benchmarks on the number of total iterations to for
    convergence by varying the probability of choosing a 'worse' move 
    for easy inputs (easy*.in) and hard inputs (hard*.in)
      
    Convergence

    Easy Input
    .001  => 1,608,918 ; 
    .005  => 647,143 ; 211,313 ; 126,902 ; 434,617 ; 335,546 ; 627,381
    .006  => 1,338,593
    .0003 => 399,604 ; 5,131,079 ; nc (Over 5 min) 
    .0004 => 54,692 ; 2,340,466
    .0005 => 1,078,154
    .0006 => 2,088,491

    Hard Input
    .01   => nc (Over 10 min) ; nc (Over 10 min)
    .001  => 70,373 ; nc
    .003  => 1,342,784
    .004  => 2,318,363
    .005  => 797,134 ; 1,025,887 ; 3,394,429 ; 2,826,606
    .0006 => 193,541
    .0008 => 1,722,199

    * nc - no convergence
