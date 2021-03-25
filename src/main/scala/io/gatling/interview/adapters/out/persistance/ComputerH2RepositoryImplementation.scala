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
					insert into computer (id, name, introduced, discontinued)
					values (${computerEntity.id}, ${computerEntity.name}, ${computerEntity.introduced}, ${computerEntity.discontinued})
					""".update.run
      .transact(xa)
      .map { id =>
        computerEntity.copy(id = id).toDomain
      }
  }

  def delete(id: Long): F[Long] = {
    sql" delete from computer where id = $id".update.run
      .transact(xa)
      .map(_.toLong)
  }

  def deleteAll(): F[Long] = {
    sql" delete from computer".update.run
      .transact(xa)
      .map(_.toLong)
  }

  def findById(id: Long): F[Option[Computer]] = {
    sql"""select id, name, introduced, discontinued
					from computer where id = $id
					"""
      .query[ComputerEntity]
      .map(_.toDomain)
      .option
      .transact(xa)
  }

  def update(computer: Computer): F[Computer] = {
    val computerEntity = ComputerEntity.toComputerEntity(computer)
    sql"""update computer
					set name = ${computerEntity.name}, introduced = ${computerEntity.introduced}, discontinued = ${computerEntity.discontinued}
					where id = ${computerEntity.id}
				  """.update.run
      .transact(xa)
      .map(id => computerEntity.copy(id = id).toDomain)
  }

  def createTableComputer: F[Int] = {
    sql"""create table if not exists computer(
					 id bigint not null AUTO_INCREMENT,
           name varchar(255) not null,
           introduced date(255),
           discontinued date(255),
           constraint pk_computer primary key (id)
          );
				  """.update.run
      .transact(xa)
  }

  def dropTableComputer: F[Int] = {
    sql"""drop table computer """.update.run
      .transact(xa)
  }
}
