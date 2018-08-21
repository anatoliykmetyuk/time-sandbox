package timesandbox

import org.fusesource.jansi.{ AnsiConsole, Ansi }, Ansi._


object implicits {
  implicit def enrichAnsi(ansi: Ansi) = new RichAnsi(ansi)
}
