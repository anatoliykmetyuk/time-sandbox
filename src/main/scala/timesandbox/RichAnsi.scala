package timesandbox

import cats._, cats.implicits._, cats.data._, cats.effect._
import org.fusesource.jansi.{ AnsiConsole, Ansi }, Ansi._


class RichAnsi(a: Ansi) {
  def box(r: Int, c: Int, h: Int, w: Int): Ansi = {
    val topWall      = "┌" + "─" * (w - 2) + "┐"
    val bottomWall   = "└" + "─" * (w - 2) + "┘"
    val intermediary = "│" + " " * (w - 2) + "│"

    (1 to h - 2).foldLeft(
      a .cursor(r        , c).render(topWall     )) { (an, i) =>
      an.cursor(r + i    , c).render(intermediary) }
        .cursor(r + h - 1, c).render(bottomWall  )
  }

  def flush: IO[Unit] = IO { println(a) }
}