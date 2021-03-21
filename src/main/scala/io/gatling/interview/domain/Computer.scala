package io.gatling.interview.domain

import java.time.LocalDate

import io.circe._
import io.circe.generic.semiauto._
import io.gatling.interview.adapters.in.presenters.ComputerPresenter
import io.gatling.interview.adapters.out.entities.ComputerEntity

final case class Computer(
	id: Long,
	name: String,
	introduced: Option[LocalDate],
	discontinued: Option[LocalDate]
) {
	def toComputerEntity: ComputerEntity = {
		ComputerEntity(id, name, introduced, discontinued)
	}

	def toComputerPresenter: ComputerPresenter = {
		ComputerPresenter(id, name, introduced, discontinued)
	}
}

object Computer {
	implicit val decoder: Decoder[Computer] = deriveDecoder
	implicit val encoder: Encoder[Computer] = deriveEncoder
}