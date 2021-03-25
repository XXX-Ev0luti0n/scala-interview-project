package io.gatling.interview.adapters.in.controllers

import cats.effect.Effect
import cats.implicits._
import io.finch._
import io.finch.circe._
import io.gatling.interview.adapters.in.presenters.ComputerPresenter
import io.gatling.interview.application.service.ComputerService

class ComputerController[F[_]: Effect](
    computerService: ComputerService[F]
) extends Endpoint.Module[F] {

  def fetchComputers: Endpoint[F, Seq[ComputerPresenter]] =
    get("computers") {
      computerService.fetchComputers.map(computers => Ok(computers))
    }

  def addComputer: Endpoint[F, String] =
    post("computer" :: jsonBody[ComputerPresenter]) { computerPresenter: ComputerPresenter =>
      computerService.findComputer(computerPresenter.id).flatMap {
        case Some(_) =>
          computerService.updateComputer(computerPresenter).map { computer =>
            Ok(s"[$computer well updated !!!]")
          }
        case _ =>
          computerService.addComputer(computerPresenter).map { computer =>
            Created(s"[$computer well added !!!]")
          }
      }
    } handle { case e: Exception =>
      Conflict(e)
    }

  def deleteComputer: Endpoint[F, String] =
    delete("computer" :: path[Long]) { id: Long =>
      computerService.deleteComputer(id).map { id =>
        Ok(s"[Computer well deleted !!!]")
      }
    } handle { case e: Exception =>
      Conflict(e)
    }

  def findComputer: Endpoint[F, String] =
    get("computer" :: path[Long]) { id: Long =>
      computerService.findComputer(id).map {
        case Some(computer) => Ok(s"[$computer found !!!]")
        case _ =>
          NotFound(
            new Exception(s"[No computer found with id $id!!!]")
          )
      }
    } handle { case e: Exception =>
      Conflict(e)
    }

}
