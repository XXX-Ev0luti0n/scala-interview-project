package io.gatling.interview.server

import java.util.concurrent.{ExecutorService, Executors}

import cats.effect._
import cats.implicits._
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finagle.{Http, ListeningServer, Service}
import com.twitter.util.{Await, Future}
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import io.finch.ToAsync
import pureconfig.ConfigSource
import pureconfig.module.catseffect.syntax.CatsEffectConfigSource

class ServerApplication[F[_] : ConcurrentEffect : ContextShift : Timer] {

	private val logger = Slf4jLogger.getLogger[F]
	private val configSource = ConfigSource.default.at("app")

	def program: F[Unit] =
		Blocker[F].use { blocker =>
			for {
				config <- configSource.loadF[F, Config](blocker)
				_ <- appResources(config).use(setup(_, config))
			} yield ()
		}

	private def setup(appResources: AppResources, config: Config): F[Unit] = {
		val api = RouterApi[F]
		for {
			server <- server(api.expose, config.server, appResources.serverExecutorService).use(_ => ConcurrentEffect[F].never.as(()))
			_ <- logger.info(s"Computer database started on port ${config.server.port}")
		} yield server
	}

	private def server(
		service: Service[Request, Response],
		config: Config.Server,
		serverExecutorService: ExecutorService
	): Resource[F, ListeningServer] =
		Resource.make(
			ConcurrentEffect[F].delay(
				Await.ready(
					Http.server
						.withExecutionOffloaded(serverExecutorService)
						.serve(s":${config.port}", service)
				)
			)
		)(s => Sync[F].suspend(implicitly[ToAsync[Future, F]].apply(s.close())))

	private def appResources(config: Config): Resource[F, AppResources] = {
		for {
			serverES <- createExecutorService(config.server.threadPoolSize)
		} yield AppResources(serverES)
	}

	private def createExecutorService(size: Int): Resource[F, ExecutorService] =
		Resource.make(ConcurrentEffect[F].delay(Executors.newFixedThreadPool(size)))(es => ConcurrentEffect[F].delay(es.shutdown()))

	private case class AppResources(serverExecutorService: ExecutorService)

}
