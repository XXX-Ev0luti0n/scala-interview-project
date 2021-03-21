package io.gatling.interview.server

import cats.effect.{ContextShift, Effect}
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import io.finch._
import io.finch.circe._
import io.gatling.interview.adapters.in.controllers.ComputerController


class RouterApi[F[_] : Effect : ContextShift] extends Endpoint.Module[F] {

	private final val computerController: ComputerController[F] = ComputerController[F]

	final val expose: Service[Request, Response] =
		Endpoint.toService(
			Bootstrap
				.serve[Application.Json](computerController.computers)
				.compile
		)

}

object RouterApi {
	def apply[F[_] : Effect : ContextShift]: RouterApi[F] = new RouterApi()
}


