package org.sudoku.io

import scala.io.Source

object Reader {
 
  def readInput: Array[Array[Int]] = {
    val fileName = "blank.in"                              
    val lines = Source.fromFile(fileName).getLines.toList
    // Initialize an empty Integer sudoku matrix
    var matrix = new Array[Array[Int]](9)
                                                          
    var i = 0
    lines.foreach { line =>
      matrix(i) = line.split(" ").map(_.toInt) 
      i += 1
    }

    // return a matrix
    matrix
  }














}
