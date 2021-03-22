package io.gatling

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.must.Matchers


class SanityCheckTest extends AnyFlatSpec with Matchers {
	it should "just compile and run sanity test" in {
		"identity" mustBe "identity"
	}
}
