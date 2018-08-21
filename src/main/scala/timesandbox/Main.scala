package timesandbox

import scala.language.postfixOps

import scala.concurrent.duration._

import cats._, cats.implicits._, cats.data._, cats.effect._
import org.fusesource.jansi.{ AnsiConsole, Ansi }, Ansi._
import timesandbox.implicits._


object Main extends IOApp with ConsoleUtils {
  val model = Matrix(
    matrix = 
    """oooooooooo
      |oooooooooo
      |oooooooooo
      |oooooooooo
      |ooooxxxooo
      |oooooooooo
      |oooooooooo
      |oooooooooo
      |oooooooooo
      |oooooooooo""".stripMargin
      .split('\n').toList  // List[String]
      .map(_.toList)       // List[List[Char]]

  , data = Map("running" -> true, "iter" -> 0)
  )

  implicit val updatePeriod = 100 milliseconds

  def run(args: List[String]): IO[ExitCode] = for {
    _   <- consoleInstall
    _   <- Time.render[Matrix, IO](model, _.data("running") == false) { s =>
      val updateState: IO[Matrix] =
        for {
          iter  <- IO { s.data("iter").asInstanceOf[Int] } 
          sNext <- IO { s.copy(
            log = s"Frame $iter" :: s.log
          , data = s.data.updated("iter", iter + 1)) }
        } yield sNext

      def checkTermination(m: Matrix): IO[Matrix] =
        for {
          mChar <- if (System.in.available > 0) IO { Some(System.in.read()) }
                   else IO.pure(None)
        } yield if (mChar.isDefined) m.copy(data = m.data.updated("running", false))
                else m
      
      updateState >>= checkTermination
    }
    _   <- consoleUninstall
  } yield ExitCode.Success
}

trait ConsoleUtils {
  val height = 30

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
