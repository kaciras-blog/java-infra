package net.kaciras.blog.infrastructure;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;

public final class Misc {

	private Misc() {}

	private static final class TrustAllManager implements X509TrustManager {
		public void checkClientTrusted(X509Certificate[] chain, String authType) {}
		public void checkServerTrusted(X509Certificate[] chain, String authType) {}
		public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
	}

	/**
	 * 屏蔽 HttpsURLConnection 默认的证书检查。
	 *
	 * @throws GeneralSecurityException 如果发生了啥错误。
	 */
	public static void disableURLConnectionCertVerify() throws GeneralSecurityException {
		var sslc = SSLContext.getInstance("TLS");
		sslc.init(null, new TrustManager[]{ new TrustAllManager() }, null);
		HttpsURLConnection.setDefaultSSLSocketFactory(sslc.getSocketFactory());
		HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
	}

	/**
	 * Helper method to get fitst element from a iterable.
	 *
	 * @param iterable iterable object.
	 * @param <T> type of element.
	 * @return the element.
	 * @throws IllegalArgumentException if iterable has no element.
	 */
	public static <T> T getFirst(Iterable<T> iterable) {
		var iter = iterable.iterator();
		if (iter.hasNext()) {
			return iter.next();
		}
		throw new IllegalArgumentException("iterable has no element.");
	}
}
