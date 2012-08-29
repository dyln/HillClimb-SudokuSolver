package org.sudoku.io

import java.io.File
import java.io.PrintWriter

object Debugger {

  def printToFile(f: File)(op: PrintWriter => Unit) {
    val p = new PrintWriter(f)
    try {
      op(p)
    } finally {
      p.close
    }
  }
}
