package io.gatling.interview.application.port.out

import io.gatling.interview.domain.Computer

trait ComputerRepository[F[_]] {

  def fetchAll: F[Seq[Computer]]

  def save(computer: Computer): F[Computer]

  def delete(id: Long): F[Long]

  def findById(id: Long): F[Option[Computer]]

  def update(computer: Computer): F[Computer]

}
