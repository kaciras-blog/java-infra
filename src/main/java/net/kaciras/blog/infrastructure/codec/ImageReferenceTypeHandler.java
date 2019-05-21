package net.kaciras.blog.infrastructure.codec;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.nio.charset.StandardCharsets;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static net.kaciras.blog.infrastructure.codec.ImageReference.HASH_SIZE;

/**
 * 图片文件的引用，该类将对Hash作为文件名的图片路径做二进制编码，存储在数据库
 * 中时比字符串更高效，但也限制了文件名的长度。
 */
public final class ImageReferenceTypeHandler extends BaseTypeHandler<ImageReference> {

	/**
	 * 编码 ImageReference 对象，将其转换为字节数组存储在数据库里，格式如下：
	 * +----------+------+
	 * | 类型序号 | 内容 |
	 * +----------+------+
	 * 类型序号为：ImageReference.getType().ordinal()，占一字节。
	 * 内容与类型相关，如果 ImageReference.getType() == ImageType.Internal 则为：
	 * +------------------+--------+
	 * | 文件名长度 1byte | 文件名 |
	 * +------------------+--------+
	 * 剩余则是文件名是文件的hash值的情况，该情况下内容为hash值。
	 *
	 * 该编码输出固定长度的字节数组，其长度为文件Hash字节长度 + 1；ImageType.Internal类型的
	 * 文件名不能超出这个长度，不足的话尾部填0。
	 *
	 * @param reference ImageReference对象
	 * @return 编码后的数据
	 */
	private byte[] encode(ImageReference reference) {
		var bytes = new byte[HASH_SIZE + 1];
		bytes[0] = (byte) reference.getType().ordinal();

		if (reference.getType() == ImageType.Internal) {
			var nameBytes = reference.getName().getBytes(StandardCharsets.UTF_8);
			if(nameBytes.length > HASH_SIZE - 1) {
				throw new IllegalArgumentException("预置图片文件名不能超过" + (HASH_SIZE - 4) + "字节");
			}
			bytes[1] = (byte) nameBytes.length;
			System.arraycopy(nameBytes, 0, bytes, 2, nameBytes.length);
		} else {
			var hash = CodecUtils.decodeHex(reference.getName());
			if(hash.length != HASH_SIZE) {
				throw new IllegalArgumentException("无效的图片Hash值");
			}
			System.arraycopy(hash, 0, bytes, 1, hash.length);
		}
		return bytes;
	}

	private ImageReference decode(byte[] bytes) {
		if(bytes == null) {
			return null;
		}
		var type = ImageType.values()[bytes[0]];
		if (type == ImageType.Internal) {
			var name = new String(bytes, 2, bytes[1], StandardCharsets.UTF_8);
			return new ImageReference(name, type);
		} else {
			return new ImageReference(CodecUtils.encodeHex(bytes, 1, HASH_SIZE), type);
		}
	}

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, ImageReference parameter, JdbcType jdbcType) throws SQLException {
		ps.setBytes(i, encode(parameter));
	}

	@Override
	public ImageReference getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return decode(rs.getBytes(columnName));
	}

	@Override
	public ImageReference getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return decode(rs.getBytes(columnIndex));
	}

	@Override
	public ImageReference getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return decode(cs.getBytes(columnIndex));
	}
}