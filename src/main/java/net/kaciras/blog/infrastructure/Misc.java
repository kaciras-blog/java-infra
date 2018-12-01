package net.kaciras.blog.infrastructure;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.lang.reflect.Field;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.NoSuchElementException;

public final class Misc {

	private Misc() {}

	private static final class TrustAllManager implements X509TrustManager {
		public void checkClientTrusted(X509Certificate[] chain, String authType) {}
		public void checkServerTrusted(X509Certificate[] chain, String authType) {}
		public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
	}

	/**
	 * 屏蔽 HttpsURLConnection 和 HttpClient(Java11) 默认的证书检查。
	 *
	 * @throws GeneralSecurityException 如果发生了啥错误。
	 */
	public static void disableURLConnectionCertVerify() throws GeneralSecurityException {
		var sslc = SSLContext.getInstance("TLS");
		sslc.init(null, new TrustManager[]{ new TrustAllManager() }, null);
		SSLContext.setDefault(sslc);
		HttpsURLConnection.setDefaultSSLSocketFactory(sslc.getSocketFactory());
		HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
	}

	public static void disableIllegalAccessWarning() {
		var javaVersionElements = System.getProperty("java.version").split("\\.");
		if (Integer.parseInt(javaVersionElements[0]) == 1) {
			return; // 1.8.x_xx or lower
		}
		try {
			var theUnsafe = Class.forName("sun.misc.Unsafe").getDeclaredField("theUnsafe");
			theUnsafe.setAccessible(true);
			var u = theUnsafe.get(null);

			var cls = Class.forName("jdk.internal.module.IllegalAccessLogger");
			var logger = cls.getDeclaredField("logger");

			var offset = (long) u.getClass()
					.getMethod("staticFieldOffset", Field.class).invoke(u, logger);

			u.getClass().getMethod("putObjectVolatile", Object.class, long.class, Object.class)
					.invoke(u, cls, offset, null);
		} catch (Exception ignore) {
			throw new UnsupportedClassVersionError("Can not desable illegal access warning");
		}
	}

	/**
	 * Helper method to get fitst element from a iterable.
	 *
	 * @param iterable iterable object.
	 * @param <T> type of element.
	 * @return the element.
	 * @throws NoSuchElementException if iterable has no element.
	 */
	public static <T> T getFirst(Iterable<T> iterable) {
		var iter = iterable.iterator();
		if (iter.hasNext()) {
			return iter.next();
		}
		throw new NoSuchElementException("iterable has no element.");
	}
}
