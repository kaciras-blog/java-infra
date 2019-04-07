package net.kaciras.blog.infrastructure.codec;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.kaciras.blog.infrastructure.exception.RequestArgumentException;

/**
 * 表示一个图片文件的引用，该类是不可变的。
 *
 * 该类也是连接前端、服务器和数据库的桥梁。向前端序列化时将被转换为图片文件的URL，在数据库中能够以更紧凑的
 * 格式来存储{@link ImageReferenceTypeHandler ImageReferenceTypeHandler}。
 * <p>
 * 此类仅表示文件名，而不包含文件所在的目录、服务器等，这些信息由前端序列化时
 * 处理 {@link ImageReferenceJson.Serializer Serializer}，{@link ImageReferenceJson.Deserializer Deserializer}。
 *
 * @author Kaciras
 */
@AllArgsConstructor
@Data
public final class ImageReference {

	/**
	 * 上传的图片文件名是32字节的摘要
	 */
	static final int HASH_SIZE = 32;

	private final String name;
	private final ImageType type;

	/**
	 * 返回原始的文件名，包含扩展名，但是不包含文件所在的目录。
	 *
	 * @return 文件名
	 */
	public String toString() {
		return type == ImageType.Internal ? name : name + '.' + type.name().toLowerCase();
	}

	/**
	 * 解析文件名，生成 ImageReference 实例。
	 *
	 * @param name 文件名
	 * @return ImageReference
	 */
	public static ImageReference parse(String name) {
		if (name == null || name.isEmpty()) {
			throw new RequestArgumentException("无效的图片文件名");
		}

		var reference = parseHex(name);
		if (reference != null) {
			return reference;
		}

		// 不是以Hash命名的文件，直接以原始文件名创建
		return new ImageReference(name, ImageType.Internal);
	}

	/**
	 * 尝试解析散列值文件名，如果文件名不符合散列值的格式则返回null
	 *
	 * @param name 文件名
	 * @return 图片引用，或者null
	 * @throws RequestArgumentException 如果文件名中出现非法字符
	 */
	private static ImageReference parseHex(String name) {
		var dot = name.lastIndexOf('.');
		var sName = name.substring(0, dot);
		var ext = name.substring(dot + 1);

		var hexChars = 0;
		for (var ch : sName.toCharArray()) {
			if (ch >= '0' && ch <= '9' || (ch >= 'a' && ch <= 'f') || (ch >= 'A' && ch <= 'F')) {
				hexChars++;
			} else if (ch == '/' || ch == '\\') {
				throw new RequestArgumentException("文件名中存在路径分隔符：" + ch);
			}
		}
		if (hexChars != ImageReference.HASH_SIZE << 1) {
			return null; // Hash长度不正确
		}

		try {
			return new ImageReference(name, ImageType.valueOf(ext.toUpperCase()));
		} catch (IllegalArgumentException e) {
			return null; // 扩展名不是定义在 ImageType 里的
		}
	}
}
