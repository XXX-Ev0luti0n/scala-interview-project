package io.gatling.interview.application.port.out

import java.time.LocalDate

import io.gatling.interview.domain.Computer

trait ComputerRepository[F[_]] {

  def fetchAll: F[Seq[Computer]]

  def save(computer: Computer): F[Computer]

  def delete(id: Long): F[Long]

  def findById(id: Long): F[Option[Computer]]

  def findByDate(date: LocalDate): F[Seq[Computer]]

  def countByDate(date: LocalDate): F[Long]

  def update(computer: Computer): F[Computer]

}
