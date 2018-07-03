package akka.grpc

import java.security.cert.Certificate

import io.grpc.CallOptions
import javax.net.ssl.SSLContext

// TODO document properties
final class GrpcClientSettings private[grpc] (
  val host: String,
  val port: Int,
  val overrideAuthority: Option[String],
  // TODO remove CallOptions here and build them ourselves inside the client
  val sslContext: Option[SSLContext],
  // TODO remove CallOptions here and build them ourselves inside the client
  val options: Option[CallOptions]) {

  def withOverrideAuthority(authority: String): GrpcClientSettings =
    new GrpcClientSettings(host, port, Some(authority), sslContext, options)
  def withSSLContext(sslContext: SSLContext): GrpcClientSettings =
    new GrpcClientSettings(host, port, overrideAuthority, Some(sslContext), options)
  def withOptions(options: CallOptions): GrpcClientSettings =
    new GrpcClientSettings(host, port, overrideAuthority, sslContext, Some(options))
  @deprecated("Use withSSLContext instead", "0.2")
  def withCertificate(certificate: String): GrpcClientSettings =
    new GrpcClientSettings(host, port, overrideAuthority, Some(TodoSSLContextUtils.trustCert(certificate)), options)

}

object GrpcClientSettings {
  /**
   * Scala API
   */
  def apply(host: String, port: Int): GrpcClientSettings =
    new GrpcClientSettings(host, port, None, Option.empty[SSLContext], None)

  /**
   * Scala API
   */
  @deprecated("Use withSSLContext instead", "0.2")
  def apply(host: String, port: Int, overrideAuthority: Option[String], options: Option[CallOptions], certificate: Option[String]): GrpcClientSettings =
    new GrpcClientSettings(host, port, overrideAuthority, certificate.map(TodoSSLContextUtils.trustCert), options)

  /**
   * Java API
   */
  def create(host: String, port: Int): GrpcClientSettings = apply(host, port)
}