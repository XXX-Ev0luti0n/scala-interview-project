package io.gatling.utils

import com.twitter.finagle.Http
import com.twitter.finagle.http.{Method, Request, Version}
import com.twitter.util.Future
import io.gatling.interview.server.Config
import io.gatling.interview.server.Config.Server

class ServerTest {
  private lazy val config = Config(
    Server(
      port = 8086,
      threadPoolSize = 1
    )
  )
  private final lazy val client = Http.client
    .withLabel("http-client-integration")
    .newService(s"localhost:${config.server.port}")

  private def requestHandler(request: Request): Future[String] =
    client(request).map(_.contentString)

  def get(url: String): Future[String] = {
    val request = Request(Version.Http11, Method.Get, url)
    requestHandler(request)
  }

  def post(url: String, data: String): Future[String] = {
    val request = Request(Version.Http11, Method.Post, url)
    request.setContentTypeJson()
    request.setContentString(data.stripMargin)
    requestHandler(request)
  }
}

object ServerTest {
  def apply(): ServerTest = new ServerTest()
}
