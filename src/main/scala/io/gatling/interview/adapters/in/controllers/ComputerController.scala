package io.gatling.interview.adapters.in.controllers

import cats.effect.Effect
import cats.implicits._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger
import io.finch._
import io.finch.circe._
import io.gatling.interview.adapters.in.presenters.ComputerPresenter
import io.gatling.interview.application.service.ComputerService

class ComputerController[F[_]: Effect]() extends Endpoint.Module[F] {

  private final val computerService: ComputerService[F] = ComputerService[F]()
  private final val logger                              = Slf4jLogger.getLogger[F]

  def fetchComputers: Endpoint[F, Seq[ComputerPresenter]] =
    get("computers") {
      computerService.fetchComputers.map(computers => Ok(computers))
    }

  def addComputer(): Endpoint[F, String] =
    post("computer" :: jsonBody[ComputerPresenter]) { computerPresenter: ComputerPresenter =>
      computerService.addComputer(computerPresenter)
      Created(s"[$computerPresenter well added !!!]")
    } handle { case e: Exception =>
      logger.warn(s"issue in adding new computer : $e")
      Conflict(e)
    }

}

object ComputerController {
  def apply[F[_]: Effect]: ComputerController[F] = new ComputerController()
}
