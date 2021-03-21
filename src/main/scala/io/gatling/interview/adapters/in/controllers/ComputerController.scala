package io.gatling.interview.adapters.in.controllers

import cats.effect.Effect
import io.finch._
import io.gatling.interview.adapters.in.presenters.ComputerPresenter
import io.gatling.interview.application.service.ComputerService

class ComputerController[F[_] : Effect]() extends Endpoint.Module[F] {

	private final val computerService: ComputerService = ComputerService()

	val computers: Endpoint[F, Seq[ComputerPresenter]] =
		get("computers") {
			val computers = computerService.fetchComputers
			Ok(computers)
		}

}

object ComputerController {
	def apply[F[_] : Effect]: ComputerController[F] = new ComputerController()
}