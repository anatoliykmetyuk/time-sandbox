package timesandbox

import scala.language.postfixOps

import scala.concurrent.duration._

import cats._, cats.implicits._, cats.data._, cats.effect._
import better.files._, better.files.File._, java.io.{ File => JFile }
import org.fusesource.jansi.{ AnsiConsole, Ansi }, Ansi._

import timesandbox.implicits._


object Main extends IOApp with ConsoleUtils {
  def run(args: List[String]): IO[ExitCode] = consoleInstall *> IO {

    val box = ansi()
      .eraseScreen(Erase.BACKWARD)
      .box(1, 2, 30, 70)
      .cursor(100, 0)

    val hw = ansi()
      .cursor(10, 0).a("Hello World")

    print(box)
    print(hw)
    print(box)
    print(hw)
  } *> consoleUninstall *> IO.pure(ExitCode.Success)
}
