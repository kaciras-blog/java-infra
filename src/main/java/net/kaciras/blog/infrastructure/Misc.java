package net.kaciras.blog.infrastructure;

import lombok.experimental.UtilityClass;

import javax.net.ssl.*;
import java.lang.reflect.Field;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.NoSuchElementException;

@UtilityClass
public class Misc {

	private class TrustAllManager extends X509ExtendedTrustManager {
		public void checkClientTrusted(X509Certificate[] certificates, String s, Socket socket)  {}
		public void checkServerTrusted(X509Certificate[] certificates, String s, Socket socket)  {}
		public void checkClientTrusted(X509Certificate[] certificates, String s, SSLEngine sslEngine) {}
		public void checkServerTrusted(X509Certificate[] certificates, String s, SSLEngine sslEngine) {}
		public void checkClientTrusted(X509Certificate[] chain, String authType) {}
		public void checkServerTrusted(X509Certificate[] chain, String authType) {}
		public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
	}

	/**
	 * 创建一个SSLContext对象，其已经初始化为接受所有证书。
	 *
	 * @return SSLContext对象
	 * @throws GeneralSecurityException 如果发生了错误
	 */
	public SSLContext createTrustAllSSLContext() throws GeneralSecurityException {
		var sslc = SSLContext.getInstance("TLS");
		sslc.init(null, new TrustManager[]{new TrustAllManager()}, null);
		return sslc;
	}

	/**
	 * 屏蔽 HttpsURLConnection 和 HttpClient(Java11) 默认的证书检查。
	 * 该方法直接修改全局设置，可能会产生副作用，使用须谨慎。
	 *
	 * @throws GeneralSecurityException 如果发生了错误
	 */
	public void disableHttpClientCertificateVerify() throws GeneralSecurityException {
		var sslc = createTrustAllSSLContext();
		SSLContext.setDefault(sslc);
		HttpsURLConnection.setDefaultSSLSocketFactory(sslc.getSocketFactory());
		HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
	}

	/**
	 * 从Java9开始的模块系统禁止了一些不合法的访问，而很多第三方库仍然依赖这些操作，不合法的
	 * 访问在程序控制台中将输出几段警告信息，看着就烦，这里给禁止掉。
	 */
	public void disableIllegalAccessWarning() {
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

			u.getClass()
					.getMethod("putObjectVolatile", Object.class, long.class, Object.class)
					.invoke(u, cls, offset, null);
		} catch (Exception ignore) {
			throw new UnsupportedClassVersionError("Can not desable illegal access warning");
		}
	}

	/**
	 * 自动检测调用者所在的类是否在Jar包里，如果是则关闭 Spring Boot 的 Dev-Tool。
	 * 请在 main() 方法中使用，并且要放在Spring启动之前。
	 *
	 * 说好的 SpringBoot dev-tool 能自动检查JAR启动的呢？
	 */
	public void disableSpringDevToolOnJarStartup() {
		var clazz = StackWalker
				.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
				.getCallerClass();
		var url = clazz.getResource('/' + clazz.getName().replace('.', '/') + ".class");
		if (url.toString().startsWith("jar:")) {
			System.setProperty("spring.devtools.restart.enabled", "false");
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
	public <T> T getFirst(Iterable<T> iterable) {
		var iter = iterable.iterator();
		if (iter.hasNext()) {
			return iter.next();
		}
		throw new NoSuchElementException("iterable has no element.");
	}
}
