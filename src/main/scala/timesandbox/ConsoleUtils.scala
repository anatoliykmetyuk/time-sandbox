package timesandbox

import scala.language.postfixOps

import cats._, cats.implicits._, cats.data._, cats.effect._
import org.fusesource.jansi.{ AnsiConsole, Ansi }, Ansi._

trait ConsoleUtils {
  val consoleInstall = IO {
    import sys.process._
    (Seq("sh", "-c", "stty -icanon min 1 < /dev/tty") !)
    (Seq("sh", "-c", "stty -echo < /dev/tty") !)
    AnsiConsole.systemInstall()
  }

  val consoleUninstall = IO {
    AnsiConsole.systemUninstall()
  }
}
