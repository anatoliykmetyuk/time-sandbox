package timesandbox

import cats._, cats.implicits._, cats.data._, cats.effect._
import org.fusesource.jansi.{ AnsiConsole, Ansi }, Ansi._


object implicits {
  implicit def enrichAnsi(ansi: Ansi) = new RichAnsi(ansi)
  implicit def deriveShow[A: ShowAnsi]: Show[A] =
    a => ShowAnsi[A].show(a).toString
}
