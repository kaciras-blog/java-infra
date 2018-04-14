package net.kaciras.blog.infrastructure.codec;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.nio.charset.StandardCharsets;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static net.kaciras.blog.infrastructure.codec.ImageRefrence.HASH_SIZE;

public final class ImageRefrenceTypeHandler extends BaseTypeHandler<ImageRefrence> {

	private byte[] encode(ImageRefrence refrence) {
		byte[] bytes = new byte[HASH_SIZE + 1];
		bytes[0] = (byte) refrence.getType().ordinal();

		if (refrence.getType() == ImageType.Internal) {
			byte[] nameBytes = refrence.getName().getBytes(StandardCharsets.UTF_8);
			if(nameBytes.length > HASH_SIZE - 1) {
				throw new IllegalArgumentException("预置图片文件名不能超过" + (HASH_SIZE - 4) + "字节");
			}
			bytes[1] = (byte) nameBytes.length;
			System.arraycopy(nameBytes, 0, bytes, 2, nameBytes.length);
		} else {
			byte[] hash = CodecUtils.decodeHex(refrence.getName());
			if(hash.length != HASH_SIZE) {
				throw new IllegalArgumentException("无效的图片Hash值");
			}
			System.arraycopy(hash, 0, bytes, 1, hash.length);
		}
		return bytes;
	}

	private ImageRefrence decode(byte[] bytes) {
		if(bytes == null) {
			return null;
		}
		ImageRefrence refrence = new ImageRefrence();
		refrence.setType(ImageType.values()[bytes[0]]);

		if (refrence.getType() == ImageType.Internal) {
			refrence.setName(new String(bytes, 2, bytes[1], StandardCharsets.UTF_8));
		} else {
			refrence.setName(CodecUtils.encodeHex(bytes, 1, HASH_SIZE));
		}
		return refrence;
	}

	@Override
	public void setNonNullParameter(PreparedStatement ps, int i, ImageRefrence parameter, JdbcType jdbcType) throws SQLException {
		ps.setBytes(i, encode(parameter));
	}

	@Override
	public ImageRefrence getNullableResult(ResultSet rs, String columnName) throws SQLException {
		return decode(rs.getBytes(columnName));
	}

	@Override
	public ImageRefrence getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
		return decode(rs.getBytes(columnIndex));
	}

	@Override
	public ImageRefrence getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
		return decode(cs.getBytes(columnIndex));
	}
}
