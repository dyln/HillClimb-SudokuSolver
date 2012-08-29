package org.sudoku.solver

import org.sudoku.io.{Reader, Writer, Debugger}

import scala.math.random

object Solver {

  def findQuadrant(i: Int, j: Int): Int = {
    (i,j) match {
      case (x,y) if x <= 2 && y <= 2 => 1
      case (x,y) if x <= 2 && y <= 5 => 2
      case (x,y) if x <= 2 && y <= 8 => 3
      case (x,y) if x <= 5 && y <= 2 => 4
      case (x,y) if x <= 5 && y <= 5 => 5 
      case (x,y) if x <= 5 && y <= 8 => 6
      case (x,y) if x <= 8 && y <= 2 => 7
      case (x,y) if x <= 8 && y <= 5 => 8
      case (x,y) if x <= 8 && y <= 8 => 9
    }
  }

  /* Method that will return a range of rows and columns for a specific indeces lookup */
  def findQuadrantRange(i: Int, j: Int): (Range, Range) = {
    (i,j) match {
      case (x,y) if x <= 2 && y <= 2 => (0 to 2, 0 to 2)
      case (x,y) if x <= 2 && y <= 5 => (0 to 2, 3 to 5) 
      case (x,y) if x <= 2 && y <= 8 => (0 to 2, 6 to 8) 
      case (x,y) if x <= 5 && y <= 2 => (3 to 5, 0 to 2) 
      case (x,y) if x <= 5 && y <= 5 => (3 to 5, 3 to 5) 
      case (x,y) if x <= 5 && y <= 8 => (3 to 5, 6 to 8) 
      case (x,y) if x <= 8 && y <= 2 => (6 to 8, 0 to 2) 
      case (x,y) if x <= 8 && y <= 5 => (6 to 8, 3 to 5) 
      case (x,y) if x <= 8 && y <= 8 => (6 to 8, 6 to 8) 
    }
  }

  // Cost is obtained by conflicting number collisions in rows and quadrants
  def obtainCost(matrix: Array[Array[Int]]) = {
    var totalCost = 0
    for (i <- 0 until matrix.length) {
      totalCost += obtainRowCost(matrix(i))
    }
    totalCost //+ obtainQuadrantCosts(matrix)
  }

  def obtainRowCost(row: Array[Int]): Int = {
    var set = Set[Int]()
    (0 /: row) { (totalCost, n) => 
      if (set contains n) totalCost + 1
      else {
        set += n
        totalCost
      }
    } 
  }

  def obtainQuadrantCost(matrix: Array[Array[Int]], rows: Range, columns: Range): Int = {
    var totalCost = 0
    var set = Set[Int]()
    for (r <- rows; c <- columns) {
      if (set contains matrix(r)(c)) totalCost += 1
      else set += matrix(r)(c)
    }
    totalCost
  }

  def obtainQuadrantCosts(matrix: Array[Array[Int]]) = {
    var totalCost = 0
    val ranges = List(0 to 2, 3 to 5, 6 to 8)
    val quadrants = for (r1 <- ranges; r2 <- ranges) yield (r1, r2)
    (0 /: quadrants) { case (totalCost, (rows, columns)) =>
      totalCost + obtainQuadrantCost(matrix, rows, columns)    
    }
  }

  /* Pick random rows that are not the same row and are not the same quadrant */
  def pickRandomRows(c: Int, r1: Int, r2: Int, fixedNumbers: Set[(Int,Int)]): (Int, Int) = {
    if (r1 == r2 || 
     (fixedNumbers contains (r1,c)) || 
     (fixedNumbers contains (r2,c))) {
      pickRandomRows(c, (random * 9).toInt, (random * 9).toInt, fixedNumbers) 
    } else {
      (r1, r2)
    }
  }

  def hillClimb(matrix: Array[Array[Int]], fixedNumbers: Set[(Int,Int)]) {

    var iterations = 0
    var cost = Integer.MAX_VALUE
    var newCost = Integer.MAX_VALUE

    //Debugger.printToFile(new File("output"))(p => {
    while (cost > 0) {
      // Choose a random column
      val column = (random * 9).toInt
   
      // Pick two random rows to swap
      val (r1, r2) = pickRandomRows(column, 0, 0, fixedNumbers)
      //p.println("("+ r1 +","+ column +"), ("+ r2 +","+ column +")")

      // Obtain the cost of the the current matrix
      cost = obtainCost(matrix)

      // Obtain the cost of swapping row values
      val oldRowCost = obtainRowCost(matrix(r1)) + obtainRowCost(matrix(r2))
      var rowcpy1 = matrix(r1).clone
      var rowcpy2 = matrix(r2).clone
      swap(column, rowcpy1, rowcpy2)
      val newRowCost = obtainRowCost(rowcpy1) + obtainRowCost(rowcpy2)

      // Obtain the cost of swapping quadrant values

      newCost = cost - (oldRowCost - newRowCost)

      // Compare costs

      println("Old cost: "+ cost +" New cost: "+ newCost)
      if (newCost <= cost)
        swap(matrix, column, r1, r2)
      else newCost = cost

      iterations += 1
    
    }
  //})
  }

  def swap(matrix: Array[Array[Int]], c: Int, r1: Int, r2: Int) {
    val aux = matrix(r1)(c)
    matrix(r1)(c) = matrix(r2)(c)
    matrix(r2)(c) = aux
  }

  def swap(i: Int, row1: Array[Int], row2: Array[Int]) {
    val aux = row1(i)
    row1(i) = row2(i)
    row2(i) = aux
  }

  def main(args: Array[String]) {
    val matrix = Reader.readInput
    Writer.printMatrix(matrix)

    val fixedNumbers = Initializer.fillMatrix(matrix)
    Writer.printQuadrants(matrix)

    hillClimb(matrix, fixedNumbers)
    Writer.printQuadrants(matrix)
    println { "Quadrant Cost: "+ obtainQuadrantCosts(matrix) }
  }
}
