package io.gatling.interview.adapters.in.presenters

import java.time.LocalDate

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import io.gatling.interview.domain.Computer

case class ComputerPresenter(
    id: Long,
    name: String,
    introduced: Option[LocalDate],
    discontinued: Option[LocalDate]
) {
  def toDomain: Computer = {
    Computer(id, name, introduced, discontinued)
  }
}

object ComputerPresenter {
  implicit val decoder: Decoder[ComputerPresenter] = deriveDecoder
  implicit val encoder: Encoder[ComputerPresenter] = deriveEncoder

  def toComputerPresenter(computer: Computer): ComputerPresenter = {
    ComputerPresenter(computer.id, computer.name, computer.introduced, computer.discontinued)
  }
}
