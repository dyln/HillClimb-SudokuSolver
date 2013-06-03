package org.sudoku.io

import scala.io.Source

object Reader {
 
  def readInput(input: String): Array[Array[Int]] = {
    val fileName = input
    val lines = Source.fromFile(fileName).getLines.toList
    // Initialize an empty Integer sudoku matrix
    var matrix = new Array[Array[Int]](9)
                                                          
    var i = 0
    lines.foreach { line =>
      if (i < 9) {
        matrix(i) = line.split(" ").map(_.toInt) 
      }
      i += 1
    }

    // return a matrix
    matrix
  }














}
