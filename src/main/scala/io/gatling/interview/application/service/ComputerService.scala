package io.gatling.interview.application.service

import java.time.LocalDate

import cats.Applicative
import cats.implicits._
import io.gatling.interview.application.port.in.presenters.ComputerPresenter
import io.gatling.interview.application.port.in.ComputerRequest
import io.gatling.interview.application.port.out.ComputerRepository

class ComputerService[F[_]: Applicative](computerAdapter: ComputerRepository[F])
    extends ComputerRequest[F] {

  //  private final val computerAdapter: ComputerMockedRepositoryImplementation[F] =
  //    ComputerMockedRepositoryImplementation[F]()

  def fetchComputers: F[Seq[ComputerPresenter]] = {
    computerAdapter.fetchAll.map { computers =>
      computers.map(ComputerPresenter.toComputerPresenter)
    }
  }

  def addComputer(computerPresenter: ComputerPresenter): F[ComputerPresenter] = {
    val computer = computerPresenter.toDomain
    computerAdapter.save(computer).map { computer =>
      ComputerPresenter.toComputerPresenter(computer)
    }
  }

  def deleteComputer(id: Long): F[Long] = {
    computerAdapter.delete(id)
  }

  def findComputer(id: Long): F[Option[ComputerPresenter]] = {
    computerAdapter.findById(id).map { computers =>
      computers.map(ComputerPresenter.toComputerPresenter)
    }
  }

  def findComputerByDate(date: LocalDate): F[Seq[ComputerPresenter]] = {
    computerAdapter.findByDate(date).map { computers =>
      computers.map(ComputerPresenter.toComputerPresenter)
    }
  }

  def updateComputer(computerPresenter: ComputerPresenter): F[ComputerPresenter] = {
    val computer = computerPresenter.toDomain
    computerAdapter.update(computer).map(ComputerPresenter.toComputerPresenter)
  }

  def countByDate(date: LocalDate): F[Long] = {
    computerAdapter.countByDate(date)
  }
}
