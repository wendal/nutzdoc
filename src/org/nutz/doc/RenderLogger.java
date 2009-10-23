package org.nutz.doc;

import java.io.IOException;
import java.io.Writer;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

public class RenderLogger {

	public RenderLogger(Writer writer) {
		this.writer = writer;
	}

	private Writer writer;

	private void log(String fmt, Object... args) {
		try {
			writer.append(String.format(fmt, args)).append('\n');
			writer.flush();
		} catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
	}

	public void log1(String fmt, Object... args) {
		log(" " + fmt, args);
	}

	public void log2(String fmt, Object... args) {
		log(Strings.dup(' ', 4) + fmt, args);
	}

	public void log3(String fmt, Object... args) {
		log(Strings.dup(' ', 8) + fmt, args);
	}

	public void log4(String fmt, Object... args) {
		log(Strings.dup(' ', 12) + fmt, args);
	}

}
