package io.gatling.interview.application.port.out

import io.gatling.interview.domain.Computer

trait ComputerRepository {

	def fetchAll: Seq[Computer]

	def addComputer(computer: Computer): Unit

	def deleteComputer(id: Long): Unit

	def findComputer(id: Long): Option[Computer]

}
