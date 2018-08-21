package timesandbox

import scala.concurrent.duration._
import cats._, cats.implicits._, cats.data._, cats.effect._

object Time {
  /**
   * A time-based rendering loop. The boolean at the end of each
   * iteration specifies whether to reiterated.
   */
  def cadencer[S, F[_]: Monad: Timer](init: S, stop: S => Boolean)(step: S => F[S])(
      implicit updatePeriod: FiniteDuration): F[S] =
    Monad[F].tailRecM(init) { state =>
      implicitly[Timer[F]].sleep(updatePeriod) *> step(state).map {
        case s if stop(s) => Right(s)
        case s            => Left (s)
      }
    }

  def render[S: Show, F[_]: Sync: Timer](init: S, stop: S => Boolean)(step: S => F[S])(
      implicit updatePeriod: FiniteDuration): F[S] =
    cadencer[S, F](init, stop) { s =>
      Sync[F].delay { println(s.show) } *> step(s) }
}