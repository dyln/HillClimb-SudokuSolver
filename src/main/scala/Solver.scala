package org.sudoku.solver

import org.sudoku.io.{ Reader, Writer }
import scala.math.random
import scala.util.Random
import scala.collection.immutable.HashSet
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{ Future, Await, Promise}
import scala.util.{ Try, Success, Failure }
import scala.concurrent.duration._

object Solver {
  type Matrix = Array[Array[Int]]
  val r = new Random

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
   if (i <= 2 && j <= 2) quadrants(0)
   else if (i <= 2 && j <= 5)  quadrants(1)
   else if (i <= 2 && j <= 8)  quadrants(2)
   else if (i <= 5 && j <= 2)  quadrants(3)
   else if (i <= 5 && j <= 5)  quadrants(4)
   else if (i <= 5 && j <= 8)  quadrants(5)
   else if (i <= 8 && j <= 2)  quadrants(6)
   else if (i <= 8 && j <= 5)  quadrants(7)
   else quadrants(8) // i <= 8 && j <= 8
  }

  // Cost is obtained by conflicting number collisions in rows and quadrants
  def obtainCost(matrix: Matrix): Int = {
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
  def obtainQuadrantCost(matrix: Matrix, rows: Array[Int], columns: Array[Int]): Int = {
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
  def obtainQuadrantCosts(matrix: Matrix) = {
    var i, totalCost = 0
    while (i < size) {
      val (rows, columns) = quadrants(i)
      totalCost += obtainQuadrantCost(matrix, rows, columns)
      i += 1
    }
    totalCost
  }

  /* Pick random rows that are distinct */
  def pickRandomRows(c: Int, r1: Int, r2: Int, fixedNumbers: HashSet[(Int, Int)]): (Int, Int) = {
    if (r1 == r2 ||
      (fixedNumbers contains (r1, c)) ||
      (fixedNumbers contains (r2, c))) {
      pickRandomRows(c, r.nextInt(9), r.nextInt(9), fixedNumbers)
    } else
      (r1, r2)
  }
  
  def formatIterations(iterations: Int): String = {
    val iterationsStr = iterations.toString
    val (fst, snd) = iterationsStr.splitAt(iterationsStr.size % 3)
    if (fst.nonEmpty)
      fst + ", " + snd.sliding(3, 3).toList.mkString("", ",", "")
    else
      snd.sliding(3, 3).toList.mkString("", ",", "")
  }

  def hillClimb(matrix: Matrix): Matrix = {

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
    if (iterations > 4000000) {
      throw new Exception("Iterations exceeded 5 million!")
    } else if (!Verifier.verifySolution(matrix)) {
      throw new Exception("Computed incorrect solution")
    }
    
    println(s"Iterations: ${formatIterations(iterations)}")

    matrix
  }

  def randomNo = {
    val r = new Random
    r.nextInt(1000)
  }

  /* Method that will swap two values in the same column but in distinct rows in a matrix */
  def swap(matrix: Matrix, c: Int, r1: Int, r2: Int) {
    val aux = matrix(r1)(c)
    matrix(r1)(c) = matrix(r2)(c)
    matrix(r2)(c) = aux
  }

  def main(args: Array[String]) {
    val matrix = Reader.readInput(args(0))
    Writer.printMatrix(matrix)

    val sudokus = List.fill(4)(Initializer.randomSudoku(matrix))
    val futureSudokus = sudokus.map(sudoku => Future(hillClimb(sudoku)))

    val solved = Await.result(Future.firstCompletedOf(futureSudokus), 3.5.minutes)
    Writer.printQuadrants(solved)
    
  }
}
