package net.kaciras.blog.infrastructure;

import com.google.common.reflect.ClassPath;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.List;
import java.util.stream.Collectors;

public final class TestHelper {

	/**
	 * 获取一个包内所有指定类的子类，不包括指定的类本身。
	 *
	 * @param clazz 指定的类
	 * @param pkg 包名
	 * @return 类列表，没有泛型因为调用方可能要转换
	 */
	@SuppressWarnings("UnstableApiUsage")
	public static <T> List getSubClassesInPackage(Class<T> clazz, String pkg) {
		try {
			return ClassPath
					.from(TestHelper.class.getClassLoader())
					.getTopLevelClasses(pkg)
					.stream()
					.map(ClassPath.ClassInfo::load)
					.filter(clazz::isAssignableFrom)
					.filter(c -> !c.equals(clazz))
					.collect(Collectors.toList());
		} catch (IOException e) {
			throw new Error("getSubClassesInPackage方法有BUG", e);
		}
	}

	/**
	 * 尽可能地获取本机的局域网地址，如果获取失败则回退到环回地址。
	 *
	 * @return 本机的局域网地址或环回地址
	 * @throws Exception 如果出了什么错
	 */
	public static InetAddress getLANAddress() throws Exception {
		var interfaces = NetworkInterface.getNetworkInterfaces();
		while (interfaces.hasMoreElements()) {
			var cur = interfaces.nextElement();
			if (cur.isLoopback() || !cur.isUp()) {
				continue;
			}
			var addrList = cur.getInterfaceAddresses();
			if(!addrList.isEmpty()) {
				return addrList.get(0).getAddress();
			}
		}
		return InetAddress.getLocalHost();
	}
}
