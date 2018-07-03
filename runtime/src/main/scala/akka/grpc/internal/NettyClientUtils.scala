/**
 * Copyright (C) 2009-2018 Lightbend Inc. <http://www.lightbend.com>
 */
package akka.grpc.internal

import java.io.File

import akka.annotation.InternalApi
import akka.grpc.GrpcClientSettings
import io.grpc.ManagedChannel
import io.grpc.netty.shaded.io.grpc.netty.{ GrpcSslContexts, NegotiationType, NettyChannelBuilder }
import io.grpc.netty.shaded.io.netty.handler.ssl.{ ClientAuth, JdkSslContext, SslContext }
import javax.net.ssl.SSLContext

/**
 * INTERNAL API
 */
@InternalApi
object NettyClientUtils {

  /**
   * INTERNAL API
   */
  @InternalApi
  def createChannel(settings: GrpcClientSettings): ManagedChannel = {
    var builder =
      NettyChannelBuilder
        .forAddress(settings.host, settings.port)
        .flowControlWindow(65 * 1024)

    builder = settings.sslContext
      .map(ctx => builder.negotiationType(NegotiationType.TLS).sslContext(nettySslContext(ctx)))
      .getOrElse(builder.negotiationType(NegotiationType.PLAINTEXT))
    builder = settings.overrideAuthority
      .map(builder.overrideAuthority(_)).getOrElse(builder)

    builder.build
  }

  private[grpc] def nettySslContext(javaSSLContext: SSLContext): SslContext =
    new JdkSslContext(javaSSLContext, true, ClientAuth.NONE) // TODO: Check ClientAuth setting?

}
