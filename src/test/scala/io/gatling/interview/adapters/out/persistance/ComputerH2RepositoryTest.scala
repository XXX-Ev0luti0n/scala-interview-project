package io.gatling.interview.adapters.out.persistance

import cats.effect._
import doobie._
import doobie.implicits._
import doobie.util.ExecutionContexts
import io.gatling.interview.adapters.out.entities.ComputerEntity
import org.specs2.mutable.Specification

class ComputerH2RepositoryTest extends Specification with doobie.specs2.IOChecker {

  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)
  override val transactor: Transactor[IO] =
    Transactor
      .fromDriverManager[IO](
        "org.h2.Driver",
        "jdbc:h2:file:~/computers:computers:DB_CLOSE_DELAY=-1"
      )

  def getComputer: doobie.Query0[Option[ComputerEntity]] =
    sql"""select id, name, introduced, discontinued
      from computer
      where id = 1
    """.query[Option[ComputerEntity]]

  def createTable: doobie.Query0[Unit] =
    sql"""create table computer (
					 id bigint not null AUTO_INCREMENT,
           name varchar(255) not null,
           introduced date(255),
           discontinued date(255),
           constraint pk_computer primary key (id)
          );
    """.query[Unit]

  check(createTable)
  check(getComputer)

}
