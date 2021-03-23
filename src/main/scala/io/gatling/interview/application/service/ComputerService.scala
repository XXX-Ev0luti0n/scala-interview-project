package io.gatling.interview.application.service

import cats.Applicative
import cats.implicits._
import io.gatling.interview.adapters.in.presenters.ComputerPresenter
import io.gatling.interview.adapters.out.persistance.ComputerMockedRepositoryImplementation
import io.gatling.interview.application.port.in.ComputerRequest

class ComputerService[F[_]: Applicative] extends ComputerRequest[F] {

  private final val computerAdapter: ComputerMockedRepositoryImplementation[F] =
    ComputerMockedRepositoryImplementation[F]()

  def fetchComputers: F[Seq[ComputerPresenter]] = {
    computerAdapter.fetchAll.map { computers =>
      computers.map(ComputerPresenter.toComputerPresenter)
    }
  }

  def addComputer(computerPresenter: ComputerPresenter): F[Unit] = {
    val computer = computerPresenter.toDomain
    computerAdapter.save(computer)
  }

  def deleteComputer(id: Long): F[Unit] = {
    computerAdapter.delete(id)
  }

  def findComputer(id: Long): F[Option[ComputerPresenter]] = {
    computerAdapter.findById(id).map { computers =>
      computers.map(ComputerPresenter.toComputerPresenter)
    }
  }

  def updateComputer(computerPresenter: ComputerPresenter): F[Unit] = {
    val computer = computerPresenter.toDomain
    computerAdapter.update(computer)
  }
}

object ComputerService {
  def apply[F[_]: Applicative](): ComputerService[F] = new ComputerService
}
