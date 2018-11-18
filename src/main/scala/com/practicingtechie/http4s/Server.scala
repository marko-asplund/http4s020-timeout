package com.practicingtechie.http4s

import cats.effect._
import cats.implicits._
import cats.temp.par._
import org.http4s.client.dsl.Http4sClientDsl
import org.http4s.dsl.Http4sDsl

import scala.language.higherKinds


class HttpServer[F[_]: ConcurrentEffect : ContextShift : Timer : Par] extends Http4sDsl[F] with Http4sClientDsl[F] {
  import cats.MonadError
  import com.typesafe.scalalogging.Logger
  import fs2.Stream
  import org.http4s.server.Router
  import org.http4s.client.Client
  import org.http4s.client.blaze.BlazeClientBuilder
  import org.http4s.server.blaze.BlazeServerBuilder

  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.duration.{Duration, SECONDS}
  import scala.util._


  val logger = Logger(this.getClass)

  def httpClient(): Stream[F, Client[F]] = BlazeClientBuilder[F](global)
    .withResponseHeaderTimeout(Duration(20, SECONDS)).stream.map { c =>
    org.http4s.client.middleware.Logger(true, true)(c)
  }

  def fooRestService(hc: Client[F])(implicit M: MonadError[F, Throwable]) = {
    import org.http4s._

    HttpRoutes.of[F] {
      case req@GET -> Root / "ping" => Ok("pong")
      case req@GET -> Root / "foo" =>
        val rq = Method.GET(Uri.unsafeFromString("https://lwn.net/"))

        hc.fetch(rq) {
          case Status.Successful(res) =>
            res.attemptAs[String].value flatMap {
              case Right(r) => Ok(r.substring(0, 100))
              case Left(ex) => M.raiseError[Response[F]](ex)
            }
          case s => M.raiseError[Response[F]](new Exception(s"error2: $s"))
        }
    }
  }

  def stream: Stream[F, ExitCode] = {
    import org.http4s.server.middleware.StaticHeaders.`no-cache`
    import org.http4s.server.middleware.CORS
    import org.http4s.implicits._

    for {
      hc <- httpClient()
      exitCode <- BlazeServerBuilder[F].
        bindHttp().
        withHttpApp(Router(
          "/api" -> CORS(`no-cache`(fooRestService(hc))),
          "/xyz" -> fooRestService(hc)
        ).orNotFound).
        serve
    } yield exitCode
  }

}


object Server extends IOApp {
  val httpServer = new HttpServer[IO]

  override def run(args: List[String]): IO[ExitCode] =
    httpServer.stream.compile.drain.as(ExitCode.Success)
}

