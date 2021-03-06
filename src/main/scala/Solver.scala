package org.sudoku.solver

import org.sudoku.io.{Reader, Writer}

import scala.math.random
import scala.util.Random
import scala.collection.immutable.HashSet

object Solver {
  // Range of indeces for the 9 quadrants
  val r1 = (0 to 2).toArray
  val r2 = (3 to 5).toArray
  val r3 = (6 to 8).toArray
  val ranges = List(r1, r2, r3)
  val quadrants = { for (rx <- ranges; ry <- ranges) yield (rx, ry) }.toArray

  // Length of a row or column in sudoku
  val size = 9

  /* Method that will return a range of rows and columns for a specific indices lookup */
  def findQuadrantRange(i: Int, j: Int): (Array[Int], Array[Int]) = {
    (i,j) match {
      case (x,y) if x <= 2 && y <= 2 => quadrants(0)
      case (x,y) if x <= 2 && y <= 5 => quadrants(1)
      case (x,y) if x <= 2 && y <= 8 => quadrants(2)
      case (x,y) if x <= 5 && y <= 2 => quadrants(3)
      case (x,y) if x <= 5 && y <= 5 => quadrants(4)
      case (x,y) if x <= 5 && y <= 8 => quadrants(5)
      case (x,y) if x <= 8 && y <= 2 => quadrants(6)
      case (x,y) if x <= 8 && y <= 5 => quadrants(7)
      case (x,y) if x <= 8 && y <= 8 => quadrants(8)
    }
  }

  // Cost is obtained by conflicting number collisions in rows and quadrants
  def obtainCost(matrix: Array[Array[Int]]): Int = {
    var i, totalCost = 0
    while (i < size) {
      totalCost += obtainRowCost(matrix(i))
      i += 1
    }
    totalCost + obtainQuadrantCosts(matrix)
  }

  /* Method will obtain the cost of a single row */
  def obtainRowCost(row: Array[Int]): Int = {
    var set = HashSet[Int]()
    var i, totalCost = 0
    while (i < size) {
      if (set contains row(i)) totalCost += 1
      else set += row(i)
      i += 1
    }
    totalCost
  }

  /* Method will obtain the cost of a single quadrant */
  def obtainQuadrantCost(matrix: Array[Array[Int]], rows: Array[Int], columns: Array[Int]): Int = {
    var r, c, totalCost = 0
    var set = HashSet[Int]()
    while (r < 3) {
      c = 0
      while (c < 3) {
        if (set contains matrix(rows(r))(columns(c))) totalCost += 1
        else set += matrix(rows(r))(columns(c))
        c += 1
      }
      r += 1
    }
    totalCost
  }

  /* Method that will obtain the total cost of each of the 9 quadrants */
  def obtainQuadrantCosts(matrix: Array[Array[Int]]) = {
    var i, totalCost = 0
    while (i < size) {
      val (rows, columns) = quadrants(i)
      totalCost += obtainQuadrantCost(matrix, rows, columns)
      i += 1
    }
    totalCost
  }

  /* Pick random rows that are distinct */
  def pickRandomRows(c: Int, r1: Int, r2: Int, fixedNumbers: HashSet[(Int,Int)]): (Int, Int) = {
    if (r1 == r2 || 
     (fixedNumbers contains (r1,c)) || 
     (fixedNumbers contains (r2,c))) {
      val r = new Random
      pickRandomRows(c, r.nextInt(9), r.nextInt(9), fixedNumbers)
    } else
      (r1, r2)
  }

  def hillClimb(matrix: Array[Array[Int]]) {

    var iterations = 0
    var cost = obtainCost(matrix)
    var newCost = Integer.MAX_VALUE
    val fixedNumbers = Initializer.fixedNumbers

    while (cost > 0 || iterations > 5000000) {
      // Choose a random column
      val column = (random * 9).toInt
   
      // Pick two random rows to swap
      val (r1, r2) = pickRandomRows(column, 0, 0, fixedNumbers)

      // Obtain the cost of rows and quadrants
      val oldRowCost = obtainRowCost(matrix(r1)) + obtainRowCost(matrix(r2))

      val quadRange1 = findQuadrantRange(r1, column)
      val quadRange2 = findQuadrantRange(r2, column)
      val oldQuadrantCosts =
        if (quadRange1 == quadRange2)
          obtainQuadrantCost(matrix, quadRange1._1, quadRange1._2) * 2
        else {
          obtainQuadrantCost(matrix, quadRange1._1, quadRange1._2) +
          obtainQuadrantCost(matrix, quadRange2._1, quadRange2._2)
        }

      // Swap the values and obtain new costs
      swap(matrix, column, r1, r2)
      val newRowCost = obtainRowCost(matrix(r1)) + obtainRowCost(matrix(r2))
      val newQuadrantCosts =
        if (quadRange1 == quadRange2)
          obtainQuadrantCost(matrix, quadRange1._1, quadRange1._2) * 2
        else {
          obtainQuadrantCost(matrix, quadRange1._1, quadRange1._2) +
          obtainQuadrantCost(matrix, quadRange2._1, quadRange2._2)
        }

      // Calculate only delta
      newCost = cost - (oldRowCost - newRowCost) - (oldQuadrantCosts - newQuadrantCosts)

      // Compare costs
      if (newCost <= cost || randomNo < 5) cost = newCost
      else swap(matrix, column, r1, r2) // If predicate not satisfied swap back to old values

      iterations += 1
    }
    if (iterations > 5000000)
      println("Iterations exceeded 5 million!")
    println(s"Iterations: $iterations")
  }

  def randomNo = {
    val r = new Random
    r.nextInt(1000)
  }

  /* Method that will swap two values in the same column but in distinct rows in a matrix */
  def swap(matrix: Array[Array[Int]], c: Int, r1: Int, r2: Int) {
    val aux = matrix(r1)(c)
    matrix(r1)(c) = matrix(r2)(c)
    matrix(r2)(c) = aux
  }

  def main(args: Array[String]) {
    val matrix = Reader.readInput(args(0))
    Writer.printMatrix(matrix)

    val sudoku = Initializer.randomSudoku(matrix)

    hillClimb(sudoku)
    println(if (Verifier.verifySolution(sudoku)) "Correct" else "Incorrect")
    Writer.printQuadrants(sudoku)
  }
}
