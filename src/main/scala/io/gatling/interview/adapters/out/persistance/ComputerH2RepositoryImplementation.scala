package io.gatling.interview.adapters.out.persistance

import cats.effect.Sync
import cats.implicits._
import doobie.implicits._
import doobie.util.transactor.Transactor
import io.gatling.interview.adapters.out.entities.ComputerEntity
import io.gatling.interview.application.port.out.ComputerRepository
import io.gatling.interview.domain.Computer

/** readme https://typelevel.org/cats-effect/typeclasses/
  */
class ComputerH2RepositoryImplementation[F[_]: Sync](xa: Transactor[F])
    extends ComputerRepository[F] {

  //  implicit val cs: ContextShift[F] = IO.contextShift(ExecutionContext.global)
  //  implicit val async: Async[F]     = ??? /*IO.async()*/
  //

  def fetchAll: F[Seq[Computer]] = {
    sql"select id, name, introduced, discontinued from computer"
      .query[ComputerEntity]
      .map(_.toDomain)
      .stream
      .compile
      .toList
      .transact(xa)
      .map(_.toSeq)
  }

  def save(computer: Computer): F[Computer] = {
    val computerEntity = ComputerEntity.toComputerEntity(computer)
    sql"""
					insert into computer ( name, introduced, discontinued)
					values ( ${computerEntity.name}, ${computerEntity.introduced}, ${computerEntity.discontinued})
					""".update.run
      .transact(xa)
      .map { id =>
        computerEntity.copy(id = id).toDomain
      }
    ???
  }

  def delete(id: Long): F[Long] = {
    sql" delete from computer where id = $id".update.run
      .transact(xa)
    ???
  }

  def findById(id: Long): F[Option[Computer]] = {
    sql"""
					select id, name, introduced, discontinued
					from computer where id = $id
					"""
      .query[ComputerEntity]
      .map(_.toDomain)
      .option
      .transact(xa)
  }

  def update(computer: Computer): F[Computer] = {
    val computerEntity = ComputerEntity.toComputerEntity(computer)
    sql"""
					update computer
					set name = ${computerEntity.name}, introduced = ${computerEntity.introduced}, discontinued = ${computerEntity.discontinued}
					where id = ${computerEntity.id}
				  """.update.run
      .transact(xa)
    ???
  }
}
