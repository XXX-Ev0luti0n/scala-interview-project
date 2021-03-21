package io.gatling.interview.adapters.out.entities

import java.time.LocalDate

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
}
