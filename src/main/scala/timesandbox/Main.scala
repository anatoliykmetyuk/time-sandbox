package timesandbox

import cats._, cats.implicits._, cats.data._, cats.effect._

import org.fusesource.jansi.AnsiConsole
import org.fusesource.jansi.Ansi, Ansi._
import org.fusesource.jansi.Ansi.Color._


object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    val consoleInstall = IO {
      import sys.process._
      (Seq("sh", "-c", "stty -icanon min 1 < /dev/tty") !)
      (Seq("sh", "-c", "stty -echo < /dev/tty") !)
      AnsiConsole.systemInstall()
    }

    val consoleUninstall = IO {
      AnsiConsole.systemUninstall()
    }

    val height = 30
    
    val output = ansi()
      .eraseScreen()
      .box(2, 4 , height, 75)
      .box(2, 80, height, 40)
      .cursor(10, 10).a("Hello World!")
      .cursor(100, 0)

    for {
      _   <- consoleInstall
      _   <- output.flush
      inp <- IO { System.in.read() }
      _   <- consoleUninstall
    } yield ExitCode.Success
  }

  implicit class RichAnsi(a: Ansi) {
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

}
