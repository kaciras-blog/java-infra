package net.kaciras.blog.infrastructure.codec;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public final class ImageRefrence {

	/** 上传的图片文件名是32字节的摘要 */
	static final int HASH_SIZE = 32;

	private String name;

	private ImageType type;

	public ImageRefrence(String name, ImageType type) {
		this.name = name;
		this.type = type;
	}

	public String toString() {
		return type == ImageType.Internal ? name : name + type.name().toLowerCase();
	}

	public static ImageRefrence internal(String name) {
		return new ImageRefrence(name, ImageType.Internal);
	}
}
