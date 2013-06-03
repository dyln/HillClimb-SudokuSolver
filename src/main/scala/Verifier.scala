package org.sudoku.solver

object Verifier {
  def verifySolution(matrix: Array[Array[Int]]): Boolean = {
    def verifyRow(row: Array[Int]): Boolean =
      row.foldLeft((true, Set.empty[Int])) { case ((verifier, set), next) =>
        (verifier && !(set contains next), set + next)
      }._1

    def verifyColumn(row: Int, col: Int, set: Set[Int], verifier: Boolean): Boolean =
      if (row >= matrix.length) verifier
      else
        verifyColumn(row + 1, col, set + matrix(row)(col), !(set contains matrix(row)(col)))

    (matrix forall verifyRow) &&
    (0 until matrix.length).foldLeft(true) { (verifier, row) => 
      verifyColumn(0, row, Set.empty[Int], verifier)
    }
  }
}
