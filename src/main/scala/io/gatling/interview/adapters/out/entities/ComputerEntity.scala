package io.gatling.interview.adapters.out.entities

import java.time.LocalDate

import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}
import io.gatling.interview.domain.Computer

final case class ComputerEntity(
    id: Long,
    name: String,
    introduced: Option[LocalDate],
    discontinued: Option[LocalDate]
) {
  def toDomain: Computer = {
    Computer(id, name, introduced, discontinued)
  }

  def isValid: Boolean = {
    name.nonEmpty
  }
}

object ComputerEntity {
  def toComputerEntity(computer: Computer): ComputerEntity = {
    ComputerEntity(computer.id, computer.name, computer.introduced, computer.discontinued)
  }

  implicit val decoder: Decoder[ComputerEntity] = deriveDecoder
  implicit val encoder: Encoder[ComputerEntity] = deriveEncoder

}
