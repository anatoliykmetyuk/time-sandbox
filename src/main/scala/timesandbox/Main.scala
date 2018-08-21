package timesandbox

import scala.language.postfixOps

import scala.concurrent.duration._

import cats._, cats.implicits._, cats.data._, cats.effect._
import better.files._, better.files.File._, java.io.{ File => JFile }
import org.fusesource.jansi.{ AnsiConsole, Ansi }, Ansi._

import timesandbox.implicits._


object Main extends IOApp with ConsoleUtils {
  implicit val updatePeriod = 100 milliseconds

  val model = Matrix(
    matrix = file"matrices/blinker.txt".contentAsString
      .split('\n').toList.map(_.toList)

  , data = Map(
      "running" -> true
    , "iter" -> 0)
  )

  def run(args: List[String]): IO[ExitCode] =
    consoleInstall *>
    Time.render[Matrix, IO](model, _.data("running") == false) { s => for {
      iter  <- IO { s.data("iter").asInstanceOf[Int] } 
      sNext <- IO { s.copy(
        log = s"Frame $iter" :: s.log
      , data = s.data.updated("iter", iter + 1)) }

      mChar <- if (System.in.available > 0) IO { Some(System.in.read()) }
               else IO.pure(None)
      sRes = if (mChar.isDefined) sNext.copy(data = sNext.data.updated("running", false))
             else sNext
    } yield sRes } *> consoleUninstall *> IO.pure(ExitCode.Success)
}
