package io.gatling.interview.adapters.out.persistance

import io.gatling.interview.adapters.out.entities.ComputerEntity
import io.gatling.interview.application.port.out.ComputerRepository
import io.gatling.interview.domain.Computer

import scala.collection.mutable

class ComputerMockedRepositoryImplementation extends ComputerRepository {

	private val computersMocked: mutable.HashMap[Long, ComputerEntity] = new mutable.HashMap()

	def fetchAll: Seq[Computer] = {
		computersMocked.values.toSeq.map(_.toDomain)
	}

	def addComputer(computer: Computer): Unit = {
		computersMocked.put(computer.id, computer.toComputerEntity)
	}

	def deleteComputer(id: Long): Unit = {
		computersMocked.remove(id)
	}

	def findComputer(id: Long): Option[Computer] = {
		computersMocked.get(id).map(_.toDomain)
	}
}

object ComputerMockedRepositoryImplementation {
	def apply(): ComputerMockedRepositoryImplementation = new ComputerMockedRepositoryImplementation()
}