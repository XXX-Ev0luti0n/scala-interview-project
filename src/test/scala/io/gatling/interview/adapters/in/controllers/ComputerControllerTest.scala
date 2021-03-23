package io.gatling.interview.adapters.in.controllers

import io.gatling.utils.ServerTest
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ComputerControllerTest extends AnyFlatSpec with Matchers {
  it should "return expected computers" in {
    val applicationTest = ServerTest()
    val expectedJson =
      """
				|[
				|   {
				|      "id":1,
				|      "name":"toto",
				|      "introduced":"2021-03-21",
				|      "discontinued":null
				|   },
				|   {
				|      "id":2,
				|      "name":"tata",
				|      "introduced":null,
				|      "discontinued":null
				|   }
				|]
				|""".stripMargin

    applicationTest.get("/computers").map { result =>
      assert(result === expectedJson)
    }
  }

}
