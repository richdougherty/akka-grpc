package akka.grpc

import java.io.File

import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts
import io.grpc.netty.shaded.io.netty.handler.ssl._
import javax.net.ssl.SSLContext

object TodoSSLContextUtils {

  def trustCert(certPath: String): SSLContext =
    buildJavaSSLContext(GrpcSslContexts.forClient.trustManager(loadCert(certPath)))
  // TODO: Replace with: forClient.trustManager(getClass.getResourceAsStream("/certs/" + name))

  private def buildJavaSSLContext(contextBuilder: SslContextBuilder): SSLContext =
    contextBuilder.sslProvider(SslProvider.JDK).applicationProtocolConfig(ApplicationProtocolConfig.DISABLED).build.asInstanceOf[JdkSslContext].context

  private def javaToNettySslContext(javaSSLContext: SSLContext): SslContext =
    new JdkSslContext(javaSSLContext, true, ClientAuth.NONE) // TODO: Check ClientAuth setting?

  private def nettyToJavaSSLContext(nettySslContext: SslContext): SSLContext = nettySslContext match {
    case jdk: JdkSslContext => jdk.context()
    case other => throw new IllegalArgumentException("Can only convert a Netty JdkSslContext to a Java SSLContext: " + other)
  }

  // FIXME couldn't we use the inputstream based trustManager() method instead of going via filesystem?
  private def loadCert(name: String): File = {
    import java.io._

    val in = new BufferedInputStream(this.getClass.getResourceAsStream("/certs/" + name))
    val inBytes: Array[Byte] = {
      val baos = new ByteArrayOutputStream(math.max(64, in.available()))
      val buffer = Array.ofDim[Byte](32 * 1024)

      var bytesRead = in.read(buffer)
      while (bytesRead >= 0) {
        baos.write(buffer, 0, bytesRead)
        bytesRead = in.read(buffer)
      }
      baos.toByteArray
    }

    val tmpFile = File.createTempFile(name, "")
    tmpFile.deleteOnExit()

    val out = new BufferedOutputStream(new FileOutputStream(tmpFile))
    out.write(inBytes)
    out.close()

    tmpFile
  }
}
