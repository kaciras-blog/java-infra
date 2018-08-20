package net.kaciras.blog.infrastructure.codec;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.kaciras.blog.infrastructure.exception.RequestArgumentException;

/**
 * 表示一个图片的文件名
 */
@NoArgsConstructor
@Data
public final class ImageRefrence {

	/**
	 * 上传的图片文件名是32字节的摘要
	 */
	static final int HASH_SIZE = 32;

	private String name;
	private ImageType type;

	/**
	 * 返回原始的文件名，包含扩展名。
	 *
	 * @return 文件名
	 */
	public String toString() {
		return type == ImageType.Internal ? name : name + '.' + type.name().toLowerCase();
	}

	/**
	 * 解析文件名，生成ImageRefrence实例。
	 *
	 * @param name 文件名
	 * @return ImageRefrence
	 */
	public static ImageRefrence parse(String name) {
		if (name == null || name.isEmpty()) {
			throw new RequestArgumentException("无效的图片文件名");
		}

		var refrence = parseHex(name);
		if (refrence != null) {
			return refrence;
		}

		// 不是以Hash命名的文件，直接以原始文件名创建
		refrence = new ImageRefrence();
		refrence.setName(name);
		refrence.setType(ImageType.Internal);
		return refrence;
	}

	private static ImageRefrence parseHex(String name) {
		int dot = name.lastIndexOf('.');
		String sName = name.substring(0, dot);
		String ext = name.substring(dot + 1);

		int hexChars = 0;
		for (char ch : sName.toCharArray()) {
			if (ch > 0x2F && ch < 0x3A || (ch > 0x40 && ch < 0x47)) {
				hexChars++;
			} else if (ch == '/' || ch == '\\') {
				throw new RequestArgumentException("文件名中存在路径分隔符：" + ch);
			}
		}
		if (hexChars != ImageRefrence.HASH_SIZE << 1) {
			return null;
		}

		ImageType type;
		try {
			type = ImageType.valueOf(ext.toUpperCase());
		} catch (IllegalArgumentException e) {
			return null;
		}

		ImageRefrence refrence = new ImageRefrence();
		refrence.setType(type);
		refrence.setName(sName);
		return refrence;
	}
}
