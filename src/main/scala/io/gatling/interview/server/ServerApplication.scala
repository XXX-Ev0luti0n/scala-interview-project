package io.gatling.interview.server

import java.util.concurrent.{ExecutorService, Executors}

import cats.effect._
import cats.implicits._
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Http, ListeningServer, Service}
import com.twitter.util.{Await, Future}
import doobie.h2.H2Transactor
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import io.finch.ToAsync
import io.gatling.interview.adapters.in.controllers.ComputerController
import io.gatling.interview.adapters.out.persistance.ComputerH2RepositoryImplementation
import io.gatling.interview.application.service.ComputerService
import pureconfig.ConfigSource
import pureconfig.module.catseffect.syntax.CatsEffectConfigSource

class ServerApplication[F[_]: ConcurrentEffect: ContextShift: Timer] {

  private val logger       = Slf4jLogger.getLogger[F]
  private val configSource = ConfigSource.default.at("app")

  def program: F[Unit] =
    Blocker[F].use { blocker =>
      for {
        config <- configSource.loadF[F, Config](blocker)
        _      <- appResources(config).use(setup(_, config))
      } yield ()
    }

  private def setup(appResources: AppResources, config: Config): F[Unit] = {
    val xa                 = appResources.transactor
    val computerAdapter    = new ComputerH2RepositoryImplementation[F](xa)
    val computerService    = new ComputerService[F](computerAdapter)
    val computerController = new ComputerController[F](computerService)
    val api                = new RouterApi[F](computerController)
    for {
      server <- server(api.expose, config.server, appResources.serverExecutorService).use(_ =>
        ConcurrentEffect[F].never.as(())
      )
      _ <- logger.info(s"Computer database started on port ${config.server.port}")
    } yield {
      server
    }
  }

  private def server(
      service: Service[Request, Response],
      config: Config.Server,
      serverExecutorService: ExecutorService
  ): Resource[F, ListeningServer] =
    Resource.make(
      ConcurrentEffect[F].delay(
          Http.server
            .withExecutionOffloaded(serverExecutorService)
            .serve(s":${config.port}", service)
      )
    )(s => Sync[F].suspend(implicitly[ToAsync[Future, F]].apply(s.close())))

  private def appResources(config: Config): Resource[F, AppResources] = {
    for {
      ce <- ExecutionContexts.fixedThreadPool[F](32)
      be <- Blocker[F]
      transactor <- H2Transactor
        .newH2Transactor[F]("jdbc:h2:file:~/computers:computers:DB_CLOSE_DELAY=-1", "", "", ce, be)
      serverES <- createExecutorService(config.server.threadPoolSize)
    } yield AppResources(serverES, transactor)
  }

  private def createExecutorService(size: Int): Resource[F, ExecutorService] =
    Resource.make(ConcurrentEffect[F].delay(Executors.newFixedThreadPool(size)))(es =>
      ConcurrentEffect[F].delay(es.shutdown())
    )

  private case class AppResources(serverExecutorService: ExecutorService, transactor: Transactor[F])

}
