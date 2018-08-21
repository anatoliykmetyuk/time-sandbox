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
, logColOffset   : Int = 0
) {
  def log(msg: String) = copy(log = msg :: log)
  
  def updateData(k: String, v: Any) = copy(data = data.updated(k, v))
  def dataAs[A](k: String) = data(k).asInstanceOf[A]

  def apply(row: Int, col: Int) = matrix(row)(col)
  def update(row: Int, col: Int, char: Char) =
    copy(matrix = matrix.updated(row, matrix(row).updated(col, char)))
}

object Matrix {
  implicit val show: ShowAnsi[Matrix] = new ShowAnsi[Matrix] {
    def show(l: Matrix): Ansi = {
      import l._
      
      // Render the layout
      val layout = ansi()
        .eraseScreen(Erase.BACKWARD)
        .box(row, col, height, widthScreen)
        .box(row, widthScreen + 5, height, widthLog)

      // Render the matrix
      val matrixRendered =
        matrix.zipWithIndex.foldLeft(layout) { case (a0, (chars, dr)) =>
          chars.zipWithIndex.foldLeft(a0)    { case (a1, (char , dc)) =>
            a1.cursor(
              row + matrixRowOffset + dr
            , col + matrixColOffset + dc
            ).render(char.toString.toUpperCase)
          } }

      // Render the log
      val logRendered =
        log.take(height - 3).reverse
        .zipWithIndex.foldLeft(matrixRendered) { case (a, (line, dr)) =>
          a.cursor(row + logRowOffset + dr, col + widthScreen + 3 + logColOffset)
           .render(line) }

      logRendered
    }
  }
}