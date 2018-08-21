package timesandbox

import scala.language.postfixOps

import scala.concurrent.duration._

import cats._, cats.implicits._, cats.data._, cats.effect._
import better.files._, better.files.File._, java.io.{ File => JFile }
import org.fusesource.jansi.{ AnsiConsole, Ansi }, Ansi._

import timesandbox.implicits._


object Main extends IOApp with ConsoleUtils {
  implicit val updatePeriod = 250 milliseconds

  val model = Matrix(
    matrix = file"matrices/blinker.txt".contentAsString.split('\n').toList.map(_.toList)
  , data = Map(
      "running" -> true
    , "iter"    -> 0
    , "paused"  -> false
    , "step"    -> false
  ))

  val h = model.matrix.length
  val w = model.matrix.head.length

  def run(args: List[String]): IO[ExitCode] =
    consoleInstall *>
    Time.render[Matrix, IO](model, _.data("running") == false) { s =>
      val iter   = s.dataAs[Int    ]("iter"  )
      val paused = s.dataAs[Boolean]("paused")
      val step   = s.dataAs[Boolean]("step"  )

      // Game of Life computation
      val sNext: Matrix = (if (!(paused && !step))
        (  0 until h).foldLeft(s ) { (s0, r) =>
          (0 until w).foldLeft(s0) { (s1, c) =>
            val alive = 'x'
            val dead  = 'o'

            val neighbours: Int = (for {
              dr <- -1 to 1
              dc <- -1 to 1

              if !(dr == 0 && dc == 0)
              nr = r + dr
              nc = c + dc

              if nr >= 0 && nr < h && nc >= 0 && nc < w
            } yield s(nr, nc)).filter(_ == alive).length

            val nextCellState = (neighbours, s(r, c)) match {
              case (n, x) if (n == 2 && x == alive) || n == 3 => alive
              case _ => dead
            }

            s1(r, c) = nextCellState
        }}
          .updateData("iter", iter + 1)
          .log(s"Step: $iter")
        else s)
        .updateData("step", false)
      
      // Controls
      (if (System.in.available > 0) IO { Some(System.in.read()) } else IO.pure(None)).map {
        case Some('q') => sNext.updateData("running", false  )
        case Some('p') => sNext.updateData("paused" , !paused)
        case Some('s') => sNext.updateData("step"   , true   )

        case Some( c ) => sNext.log(s"Unknown key: ${c.toChar}")
        case None      => sNext
      }
    } *> consoleUninstall *> IO.pure(ExitCode.Success)
}
