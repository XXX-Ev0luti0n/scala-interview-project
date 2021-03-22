package io.gatling.interview.domain

import java.time.LocalDate

import io.circe._
import io.circe.generic.semiauto._

final case class Computer(
	id: Long,
	name: String,
	introduced: Option[LocalDate],
	discontinued: Option[LocalDate]
)

object Computer {
	implicit val decoder: Decoder[Computer] = deriveDecoder
	implicit val encoder: Encoder[Computer] = deriveEncoder
}