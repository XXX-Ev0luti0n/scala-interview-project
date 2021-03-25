package io.gatling.interview.application.port.in

import java.time.LocalDate

import io.gatling.interview.adapters.in.presenters.ComputerPresenter

trait ComputerRequest[F[_]] {

  def fetchComputers: F[Seq[ComputerPresenter]]

  def addComputer(computerPresenter: ComputerPresenter): F[ComputerPresenter]

  def deleteComputer(id: Long): F[Long]

  def findComputer(id: Long): F[Option[ComputerPresenter]]

  def updateComputer(computerPresenter: ComputerPresenter): F[ComputerPresenter]

  def findComputerByDate(date: LocalDate): F[Seq[ComputerPresenter]]

  def countByDate(date: LocalDate): F[Long]

}
