/*
 * Copyright (C) 2018 Lightbend Inc. <https://www.lightbend.com>
 */

package com.lightbend.grpc.interop

import akka.actor.ActorSystem
import akka.stream.{ ActorMaterializer, Materializer }
import io.grpc.internal.testing.TestUtils
import io.grpc.testing.integration.Util
import io.grpc.testing.integration2.{ ClientTester, Settings, TestServiceClient }

import scala.concurrent.duration._
import scala.concurrent.{ Await, ExecutionContext }

// TODO #151 use our own Settings object
final case class AkkaGrpcClientScala(clientTesterFactory: Settings => Materializer => ExecutionContext => ClientTester) extends GrpcClient {

  override def run(args: Array[String]): Unit = {
    TestUtils.installConscryptIfAvailable()
    val settings = Settings.parseArgs(args)

    implicit val sys = ActorSystem()
    implicit val mat = ActorMaterializer()

    val client = new TestServiceClient(clientTesterFactory(settings)(mat)(sys.dispatcher))
    client.setUp()

    try
      client.run(settings)
    finally {
      client.tearDown()
      Await.result(sys.terminate(), 10.seconds)
    }
  }

}
