package io.gatling.interview.adapters.out.persistance

import java.time.LocalDate

import cats.Applicative
import cats.implicits._
import io.gatling.interview.application.port.out.entities.ComputerEntity
import io.gatling.interview.application.port.out.ComputerRepository
import io.gatling.interview.application.domain.Computer

import scala.collection.mutable

class ComputerMockedRepositoryImplementation[F[_]: Applicative] extends ComputerRepository[F] {

  private val computersMocked: mutable.HashMap[Long, ComputerEntity] = new mutable.HashMap()

  def fetchAll: F[Seq[Computer]] = {
    Applicative[F].pure(computersMocked.values.toSeq.map(_.toDomain))
  }

  def save(computer: Computer): F[Computer] = {
    for {
      maybeData <- findById(computer.id)
    } yield {
      maybeData match {
        case Some(data) => {
          updateAux(data)
        }
        case _ => {
          computersMocked.put(computer.id, ComputerEntity.toComputerEntity(computer))
          computer
        }
      }
    }

  }

  def delete(id: Long): F[Long] = {
    Applicative[F].pure({
      computersMocked.remove(id).map(_.id).getOrElse(0L)
    })
  }

  def findById(id: Long): F[Option[Computer]] = {
    Applicative[F].pure(computersMocked.get(id).map(_.toDomain))
  }

  private def updateAux(computer: Computer) = {
    computersMocked.update(computer.id, ComputerEntity.toComputerEntity(computer))
    computer
  }

  def update(computer: Computer): F[Computer] = {
    Applicative[F].pure(
      updateAux(computer)
    )
  }

  def findByDate(date: LocalDate): F[Seq[Computer]] = {
    Applicative[F].pure(
      Seq.empty
    )
  }

  def countByDate(date: LocalDate): F[Long] = {
    Applicative[F].pure(
      0L
    )
  }
}
