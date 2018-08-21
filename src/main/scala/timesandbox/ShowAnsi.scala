package timesandbox

import cats._, cats.implicits._, cats.data._, cats.effect._

import org.fusesource.jansi.{ AnsiConsole, Ansi }, Ansi._
import simulacrum._

@typeclass trait ShowAnsi[A] {
  def show(a: A): Ansi
}

object ShowAnsi {
  implicit def deriveShow[A: ShowAnsi]: Show[A] =
    a => ShowAnsi[A].show(a).toString
}
