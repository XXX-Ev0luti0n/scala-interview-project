package io.gatling.interview.application.port.in

import io.gatling.interview.adapters.in.presenters.ComputerPresenter

trait ComputerRequest {

	def fetchComputers: Seq[ComputerPresenter]

	def addComputer(): Unit

	def deleteComputer(): Unit

	def updateComputer(): ComputerPresenter

}
