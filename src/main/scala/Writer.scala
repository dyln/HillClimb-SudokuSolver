package org.sudoku.io

object Writer {

  def printMatrix(matrix: Array[Array[Int]]) {
    def stringRepr(m: Array[Array[Int]]) = {
      ("" /: matrix) { (str, row) =>
        str + row.mkString("", " ", "\n")
      }
    }
    println { stringRepr(matrix) }
  }

  def printQuadrants(matrix: Array[Array[Int]]) {
    def printQuadrant(quadrant: Range) {
      for (i <- quadrant) {
        for (j <- 0 until matrix.length) {
          if (j % 3 == 0) print("  "+ matrix(i)(j) +" ")
          else print(matrix(i)(j) +" ")
        }
        println
      }
      println
    }

    val quadrants = List(0 to 2, 3 to 5, 6 to 8)
    quadrants foreach printQuadrant
  }

}

