package io.gatling.interview.application.port.in

import io.gatling.interview.adapters.in.presenters.ComputerPresenter

trait ComputerRequest[F[_]] {

	def fetchComputers: F[Seq[ComputerPresenter]]

	def addComputer(computerPresenter: ComputerPresenter): F[Unit]

	def deleteComputer(id: Long): F[Unit]

	def findComputer(id: Long): F[Option[ComputerPresenter]]

	def updateComputer(computerPresenter: ComputerPresenter): F[Unit]

}
