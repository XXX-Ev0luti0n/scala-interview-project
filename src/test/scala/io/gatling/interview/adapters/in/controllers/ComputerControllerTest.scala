package io.gatling

import java.time.LocalDate

import cats.effect.{ContextShift, IO}
import com.twitter.finagle.http.Status
import com.twitter.io.Buf
import doobie.Transactor
import doobie.util.ExecutionContexts
import io.finch.{Application, Input, Output}
import io.gatling.interview.adapters.in.controllers.ComputerController
import io.gatling.interview.adapters.in.presenters.ComputerPresenter
import io.gatling.interview.adapters.out.persistance.ComputerH2RepositoryImplementation
import io.gatling.interview.application.service.ComputerService
import io.gatling.interview.domain.Computer
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ComputerControllerTest extends AnyFlatSpec with Matchers with BeforeAndAfterAll {
  behavior of "the computer endpoint"
  implicit final val cs: ContextShift[IO] = IO.contextShift(ExecutionContexts.synchronous)
  val transactor: Transactor[IO] = Transactor
    .fromDriverManager[IO](
      "org.h2.Driver",
      "jdbc:h2:file:~/controller:computers:DB_CLOSE_DELAY=-1"
    )
  val computerAdapter    = new ComputerH2RepositoryImplementation[IO](transactor)
  val computerService    = new ComputerService[IO](computerAdapter)
  val computerController = new ComputerController[IO](computerService)

  override def beforeAll(): Unit = {
    computerAdapter.createTableComputer.unsafeRunSync()
    (10 to 11).map { id =>
      computerAdapter.save(Computer(id, s"name-$id", Some(LocalDate.now()), None)).unsafeRunSync()
    }
    println("Init test env")
  }

  override def afterAll(): Unit = {
    //    computerAdapter.deleteAll().unsafeRunSync()
    computerAdapter.dropTableComputer.unsafeRunSync()
    println("cleaning test env ")
  }

  it should "[POST] - add computer works" in {
    val computerJson: Buf = Buf.Utf8(
      """
        |{
        |      "id": 1,
        |      "name": "MacBook Pro 15.4 inch"
        |    }
        |""".stripMargin
    )
    computerController
      .addComputer(Input.post("/computer").withBody[Application.Json](computerJson))
      .awaitOutputUnsafe() shouldBe Some(
      Output.payload[String](
        "[Computer MacBook Pro 15.4 inch with id : 1 well added !!!]",
        Status(201)
      )
    )
  }

  it should "[POST] - update computer works" in {
    val computerJson: Buf = Buf.Utf8(
      """
        |{
        |      "id": 1,
        |      "name": "Artichaut"
        |    }
        |""".stripMargin
    )
    computerController
      .addComputer(Input.post("/computer").withBody[Application.Json](computerJson))
      .awaitOutputUnsafe() shouldBe Some(
      Output.payload[String](
        "[Computer Artichaut with id : 1 well updated !!!]",
        Status(200)
      )
    )
  }

  it should "[DELETE] - remove computer works" in {
    val id = 2
    computerController
      .deleteComputer(Input.delete(s"/computer/$id"))
      .awaitOutputUnsafe() shouldBe Some(
      Output.payload[String](
        "[Computer well deleted !!!]",
        Status(200)
      )
    )
  }

  it should "[DELETE] - remove computer failed" in {
    val id = 999
    computerController
      .deleteComputer(Input.delete(s"/computer/$id"))
      .awaitOutputUnsafe()
      .map(_.status) shouldBe Some(Status.Ok)
  }

  it should "[GET] - find computer works" in {
    val id = 1
    computerController
      .findComputer(Input.get(s"/computer/$id"))
      .awaitOutputUnsafe() shouldBe Some(
      Output.payload[String](
        "[Computer Artichaut with id : 1 found !!!]",
        Status(200)
      )
    )
  }

  it should "[GET] - find computer failed with status 404" in {
    val id = 999
    computerController
      .findComputer(Input.get(s"/computer/$id"))
      .awaitOutputUnsafe()
      .map(_.status) shouldBe Some(Status(404))

  }

  it should "[GET] - find computer with filter date works" in {

    val date = "2020-10-10"
    computerController
      .findComputerByDate(Input.get(s"/computers/date/$date"))
      .awaitOutputUnsafe()
      .map(_.value) shouldBe Some(
      Seq(
        ComputerPresenter(10, "name-10", Some(LocalDate.now()), None),
        ComputerPresenter(11, "name-11", Some(LocalDate.now()), None)
      )
    )
  }

  it should "[GET] - count computer after given date works" in {
    val date = "2020-10-10"
    computerController
      .count(Input.get(s"/computers/count/$date"))
      .awaitOutputUnsafe()
      .map(_.value) shouldBe Some(
      "There are 2 computer introduced after 2020-10-10"
    )
  }
}
