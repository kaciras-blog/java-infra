package net.kaciras.blog.infrastructure.codec;

/**
 * 表示图片类型。
 * 序列化可能使用了字段次序，所以请勿改动顺序，新增的往后加。
 */
public enum ImageType {

	/** 内置的图片，文件名不是Hash值 */
	Internal,

	JPG, JPEG, PNG, GIF, SVG, BMP,
}
