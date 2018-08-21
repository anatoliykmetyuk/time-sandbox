package timesandbox

import scala.language.postfixOps

import cats._, cats.implicits._, cats.data._, cats.effect._
import org.fusesource.jansi.{ AnsiConsole, Ansi }, Ansi._

trait ConsoleUtils {
  import sys.process._

  val consoleInstall = IO {
    Seq("sh", "-c", "stty -icanon min 1 < /dev/tty").!  // Consume one character at a time
    Seq("sh", "-c", "stty -echo < /dev/tty").!          // Do not display user input
    Seq("tput", "civis").!                              // Make cursor invisible

    AnsiConsole.systemInstall()
  }

  val consoleUninstall = IO {
    AnsiConsole.systemUninstall()
    Seq("tput", "cnorm").!  // Make cursor visible
  }
}
