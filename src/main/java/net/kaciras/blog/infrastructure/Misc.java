package net.kaciras.blog.infrastructure;

import lombok.experimental.UtilityClass;
import sun.misc.Unsafe;

import javax.net.ssl.*;
import javax.servlet.http.HttpServletRequest;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.security.cert.X509Certificate;
import java.util.NoSuchElementException;

@UtilityClass
public class Misc {

	//@formatter:off
	private static final class TrustAllManager extends X509ExtendedTrustManager {
		public void checkClientTrusted(X509Certificate[] certificates, String s, Socket socket)  {}
		public void checkServerTrusted(X509Certificate[] certificates, String s, Socket socket)  {}
		public void checkClientTrusted(X509Certificate[] certificates, String s, SSLEngine sslEngine) {}
		public void checkServerTrusted(X509Certificate[] certificates, String s, SSLEngine sslEngine) {}
		public void checkClientTrusted(X509Certificate[] chain, String authType) {}
		public void checkServerTrusted(X509Certificate[] chain, String authType) {}
		public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
	}
	//@formatter:on

	/**
	 * 创建一个SSLContext对象，其被初始化为接受所有证书。
	 *
	 * @return SSLContext对象
	 * @throws GeneralSecurityException 如果发生了错误
	 */
	public static SSLContext createTrustAllSSLContext() throws GeneralSecurityException {
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
	public static void disableHttpClientCertificateVerify() throws GeneralSecurityException {
		var sslc = createTrustAllSSLContext();
		SSLContext.setDefault(sslc);
		HttpsURLConnection.setDefaultSSLSocketFactory(sslc.getSocketFactory());
		HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
	}

	/**
	 * 从Java9开始的模块系统禁止了一些不合法的访问，而很多第三方库仍然依赖这些操作，不合法的
	 * 访问在程序控制台中将输出几段警告信息，看着就烦，这里给禁止掉。
	 * <p>
	 * 具体做法是把 IllegalAccessLogger.logger 提前设置成 null，因为它是 OneShot 机制，只使用
	 * 一次之后就被设置为 null 避免重复打印。
	 * <p>
	 * 该修改过程本身就属于非法访问，为了不触发警告，必须用 Unsafe 里的方法来修改而不能用反射。
	 */
	public static void disableIllegalAccessWarning() {
		try {
			var theUnsafe = Class.forName("sun.misc.Unsafe").getDeclaredField("theUnsafe");
			theUnsafe.setAccessible(true);
			var u = (Unsafe) theUnsafe.get(null);

			var cls = Class.forName("jdk.internal.module.IllegalAccessLogger");
			var logger = cls.getDeclaredField("logger");
			u.putObjectVolatile(cls, u.staticFieldOffset(logger), null);
		} catch (ClassNotFoundException ignore) {
		} catch (Exception e) {
			throw new Error("An error occurred when disable illegal access warning", e);
		}
	}

	/**
	 * 自动检测调用者所在的类是否在Jar包里，如果是则关闭 Spring Boot 的 Dev-Tool。
	 * 请在 main() 方法中使用，并且要放在Spring启动之前。
	 * <p>
	 * 说好的 SpringBoot dev-tool 能自动检查JAR启动的呢？
	 */
	public static void disableSpringDevToolOnJarStartup() {
		var clazz = StackWalker
				.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
				.getCallerClass();
		var url = clazz.getResource('/' + clazz.getName().replace('.', '/') + ".class");
		if (url.toString().startsWith("jar:")) {
			System.setProperty("spring.devtools.restart.enabled", "false");
		}
	}

	/**
	 * Helper method to get first element from a iterable.
	 *
	 * @param iterable iterable object.
	 * @param <T>      type of the element.
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

	/**
	 * 判断一个请求对象是否是不改变状态的安全请求。安全请求的定义见：
	 * https://tools.ietf.org/html/rfc7231#section-4.2.1
	 * 注意这里去掉了 TRACE 方法，因为我用不到它，而且它的功能还有些安全隐患。
	 *
	 * @param request 请求对象
	 * @return 如果是安全请求则为true，否则false
	 */
	@SuppressWarnings("BooleanMethodIsAlwaysInverted")
	public static boolean isSafeRequest(HttpServletRequest request) {
		var method = request.getMethod();
		return "GET".equals(method) || "HEAD".equals(method) || "OPTIONS".equals(method);
	}
}
