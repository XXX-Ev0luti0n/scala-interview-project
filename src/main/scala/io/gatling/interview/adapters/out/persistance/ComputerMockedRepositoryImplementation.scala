package io.gatling.interview.adapters.out.persistance

import cats.Applicative
import cats.implicits._
import io.gatling.interview.adapters.out.entities.ComputerEntity
import io.gatling.interview.application.port.out.ComputerRepository
import io.gatling.interview.domain.Computer

import scala.collection.mutable

class ComputerMockedRepositoryImplementation[F[_]: Applicative] extends ComputerRepository[F] {

  private val computersMocked: mutable.HashMap[Long, ComputerEntity] = new mutable.HashMap()

  def fetchAll: F[Seq[Computer]] = {
    Applicative[F].pure(computersMocked.values.toSeq.map(_.toDomain))
  }

  def save(computer: Computer): F[Unit] = {
    for {
      maybeData <- findById(computer.id)
    } yield {
      maybeData match {
        case Some(data) => update(data)
        case _ =>
          Applicative[F].pure(
            computersMocked.put(computer.id, ComputerEntity.toComputerEntity(computer))
          )
      }
    }

  }

  def delete(id: Long): F[Unit] = {
    //		computersMocked.remove(id).pure[F[Unit]]
    ???
  }

  def findById(id: Long): F[Option[Computer]] = {
    Applicative[F].pure(computersMocked.get(id).map(_.toDomain))
  }

  def update(computer: Computer): F[Unit] = {
    Applicative[F].pure(
      computersMocked.update(computer.id, ComputerEntity.toComputerEntity(computer))
    )
  }
}

object ComputerMockedRepositoryImplementation {
  def apply[F[_]: Applicative](): ComputerMockedRepositoryImplementation[F] =
    new ComputerMockedRepositoryImplementation
}
