package org.sudoku.solver

import scala.math.random
import scala.collection.immutable.HashSet

object Initializer {
  
  var fixedNumbers = HashSet[(Int,Int)]()

  def addFixedNumbers(matrix: Array[Array[Int]], column: Int): Set[Int] = {
    var set = Set[Int]()
    for (row <- 0 until matrix.length) {
      if (matrix(row)(column) > 0) {
        set += matrix(row)(column)
        fixedNumbers += (row -> column)
      }
    }
    set
  }

  // generate a random sudoku
  def randomSudoku(m: Array[Array[Int]]) = {
   val matrix = m.map(_.clone) 
   for (i <- 0 until matrix.length) {
      var set = addFixedNumbers(matrix, i)
      for (j <- 0 until matrix.length) {
        if (matrix(j)(i) > 0) set += matrix(j)(i)  
        else {
          // Generate a random non repeated number
          var number = 0
          do {
            number = (random * 9).toInt + 1
          } while (set contains number)
                                                     
          // Add number to the column and to the set
          matrix(j)(i) = number
          set += number
        }
      }
    }
                                                      
    matrix 
  }

}
