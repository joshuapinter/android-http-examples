package com.publicobject.http.certs;

import com.squareup.okhttp.OkHttpClient;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * This example makes an HTTPS connection to a server without validating that
 * the TLS certificate is trusted. This is dangerous and bad for production, but
 * very convenient for development as it allows trusted man-in-the-middle tools
 * like Charles Web Debugging Proxy to observe all of our HTTPS traffic.
 */
public class NoCertsExample {
  final OkHttpClient okHttpClient = new OkHttpClient();

  public void get(String url) throws IOException {
    HttpURLConnection connection = okHttpClient.open(new URL(url));
    okHttpClient.setSslSocketFactory(badSslSocketFactory());
    printResponse(connection.getInputStream());
  }

  /**
   * Returns an SSL socket factory that doesn't validate SSL certs. This should
   * only be used for development.
   */
  private static SSLSocketFactory badSslSocketFactory() {
    try {
      // Construct SSLSocketFactory that accepts any cert.
      SSLContext context = SSLContext.getInstance("TLS");
      TrustManager permissive = new X509TrustManager() {
        @Override public void checkClientTrusted(X509Certificate[] chain,
            String authType) throws CertificateException {}
        @Override public void checkServerTrusted(X509Certificate[] chain,
            String authType) throws CertificateException {}
        @Override public X509Certificate[] getAcceptedIssuers() {
          return null;
        }
      };
      context.init(null, new TrustManager[] { permissive }, null);
      return context.getSocketFactory();
    } catch (Exception e) {
      throw new AssertionError(e);
    }
  }

  private void printResponse(InputStream in) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
    for (String line; (line = reader.readLine()) != null; ) {
      System.out.println(line);
    }
  }

  public static void main(String[] args) throws IOException {
    String url = "https://www.wellsfargo.com/";
    new NoCertsExample().get(url);
  }
}
