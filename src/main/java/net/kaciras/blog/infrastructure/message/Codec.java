package net.kaciras.blog.infrastructure.message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Codec {

	void serialize(OutputStream out, Event object) throws IOException;

	Event deserialize(InputStream in) throws IOException;
}
