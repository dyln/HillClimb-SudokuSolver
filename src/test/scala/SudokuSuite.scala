import org.scalatest.FunSuite

class SudokuSuite extends FunSuite {
  import org.sudoku.solver.{Initializer, Solver, Verifier}
  import org.sudoku.io.Writer

  def makeSudokuFromString(str: String) =
    str.stripMargin.split("\n").map(_.split("""\s""").map(_.toInt))

  test("verifier: check that the solution verifier works") {
    val str =
      """|1 4 2 9 6 7 8 3 5
         |7 3 5 4 8 1 6 2 9
         |8 9 6 5 3 2 1 4 7
         |9 7 3 1 4 8 5 6 2
         |5 6 1 2 7 9 4 8 3
         |2 8 4 6 5 3 9 7 1
         |3 5 7 8 9 4 2 1 6
         |4 2 9 3 1 6 7 5 8
         |6 1 8 7 2 5 3 9 4"""
    val sudoku = makeSudokuFromString(str)
    assert(Verifier.verifySolution(sudoku), "Incorrectly verifies the solution, the solution should be correct")
  }

  test("solver: solves a blank 9x9 sudoku correctly") {
    val str=
      """|0 0 0 0 0 0 0 0 0
         |0 0 0 0 0 0 0 0 0
         |0 0 0 0 0 0 0 0 0
         |0 0 0 0 0 0 0 0 0
         |0 0 0 0 0 0 0 0 0
         |0 0 0 0 0 0 0 0 0
         |0 0 0 0 0 0 0 0 0
         |0 0 0 0 0 0 0 0 0
         |0 0 0 0 0 0 0 0 0"""
    val sudoku = (makeSudokuFromString _ andThen Initializer.randomSudoku)(str)
    Solver hillClimb sudoku
    assert(Verifier.verifySolution(sudoku), s"${Writer.stringRepr(sudoku)}")
  }

  test("solver: solves an easy 9x9 sudoku correctly") {
    val str =
      """|7 0 0 0 0 9 0 0 6
         |0 1 0 0 0 5 3 7 0
         |0 0 4 7 6 0 0 0 0
         |0 0 6 0 0 8 0 3 0
         |0 0 2 0 4 0 6 0 0
         |0 3 0 2 0 0 1 0 0
         |0 0 0 0 8 2 7 0 0
         |0 6 7 1 0 0 0 2 0
         |3 0 0 5 0 0 0 0 8"""

    val sudoku = (makeSudokuFromString _ andThen Initializer.randomSudoku)(str)
    Solver hillClimb sudoku
    assert(Verifier.verifySolution(sudoku), s"${Writer.stringRepr(sudoku)}")
  }


}
