package io.gatling.interview.server

import cats.effect.{ContextShift, Effect}
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import io.finch._
import io.finch.circe._
import io.gatling.interview.adapters.in.controllers.ComputerController

class RouterApi[F[_]: Effect: ContextShift](computerController: ComputerController[F])
    extends Endpoint.Module[F] {

  final val expose: Service[Request, Response] =
    Endpoint.toService(
      Bootstrap
        .serve[Application.Json](computerController.fetchComputers)
        .serve[Application.Json](computerController.addComputer)
        .serve[Application.Json](computerController.findComputer)
        .serve[Application.Json](computerController.findComputerByDate)
        .serve[Application.Json](computerController.count)
        .serve[Application.Json](computerController.deleteComputer)
        .compile
    )

}
