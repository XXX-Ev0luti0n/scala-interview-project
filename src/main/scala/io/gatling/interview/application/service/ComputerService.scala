package io.gatling.interview.application.service

import io.gatling.interview.adapters.in.presenters.ComputerPresenter
import io.gatling.interview.adapters.out.persistance.ComputerMockedRepositoryImplementation
import io.gatling.interview.application.port.in.ComputerRequest

class ComputerService extends ComputerRequest {

	private final val computerAdapter = ComputerMockedRepositoryImplementation()

	def fetchComputers: Seq[ComputerPresenter] = {
		computerAdapter.fetchAll.map(_.toComputerPresenter)
	}

	def addComputer(): Unit = {
		computerAdapter.addComputer(???)
	}

	override def deleteComputer(): Unit = ???

	override def updateComputer(): ComputerPresenter = ???
}

object ComputerService {
	def apply(): ComputerService = new ComputerService()
}
