package timesandbox

import cats._, cats.implicits._, cats.data._, cats.effect._
import org.fusesource.jansi.{ AnsiConsole, Ansi }, Ansi._
import timesandbox.implicits._


case class Matrix(
  matrix     : List[List[Char]]
, log        : List[String]     = Nil
, data       : Map[String, Any] = Map()

, row        : Int = 2
, col        : Int = 4
, height     : Int = 30
, widthScreen: Int = 75
, widthLog   : Int = 40

, matrixRowOffset: Int = 3
, matrixColOffset: Int = 6
, logRowOffset   : Int = 1
, logColOffset   : Int = 0)

object Matrix {
  implicit val show: Show[Matrix] = new Show[Matrix] {
    def show(l: Matrix): String = {
      import l._
      
      // Render the layout
      val layout = ansi()
        .eraseScreen()
        .box(row, col, height, widthScreen)
        .box(row, widthScreen + 5, height, widthLog)

      // Render the matrix
      val matrixRendered =
        matrix.zipWithIndex.foldLeft(layout) { case (a0, (chars, dr)) =>
          chars.zipWithIndex.foldLeft(a0)    { case (a1, (char , dc)) =>
            a1.cursor(
              row + matrixRowOffset + dr
            , col + matrixColOffset + dc
            ).a(char)
          } }

      // Render the log
      val logRendered =
        log.take(height - 3).reverse
        .zipWithIndex.foldLeft(matrixRendered) { case (a, (line, dr)) =>
          a.cursor(row + logRowOffset + dr, col + widthScreen + 3 + logColOffset)
           .render(line) }

      // Convert Ansi to String
      logRendered.cursor(100, 0).toString
    }
  }
}