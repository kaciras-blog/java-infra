package net.kaciras.blog.infrastructure.typesession;

import java.io.InputStream;
import java.io.OutputStream;

public interface DefiniteSerializer {

	void serialize(Object value, OutputStream out);

	<T> T deserialize(InputStream in, Class<T> type);
}
