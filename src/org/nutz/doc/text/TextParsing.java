package org.nutz.doc.text;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.regex.Pattern;

import org.nutz.doc.meta.ZDoc;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;

/**
 * 
 * <pre>
 * WorkingStack
 * 		&gt; descide the CharAcceptor
 * 
 * CharAcceptor  abstract
 * 		&gt; accept(char c)
 * 		&gt; set(ZDoc zdoc)
 * 		&gt; support nesting
 * 
 * impl: Link,Author, Title,
 * 
 * </pre>
 * 
 * @author zozoh
 * 
 */
class TextParsing {

	private File src;
	private Reader reader;
	private ZDoc doc;
	private char c;
	private char prev;

	TextParsing(File src) {
		this.src = src;
		ZDoc doc = new ZDoc();
		doc.setSource(src);
	}

	void open() {
		reader = new BufferedReader(Streams.fileInr(src));
	}

	void close() throws IOException {
		reader.close();
	}

	ZDoc getDoc() {
		return doc;
	}

	char next() throws IOException {
		prev = c;
		c = (char) reader.read();
		return c;
	}

	void process() throws IOException {
		WorkingStack stack = new WorkingStack(doc);
		while (-1 != next()) {
			if (prev == '\\') {
				if (c == '\r' || c == '\n') {
					do {
						next();
						if (c == -1)
							break;
					} while ('\r' == c || '\n' == c);
				}
			} else if (c == '\\')
				continue;

			stack.accept(c);
		}
		stack.close();
		formatList();
	}

	private static Pattern UL = Pattern.compile("^[ \t]*[*][ ].*$");
	private static Pattern OL = Pattern.compile("^[ \t]*[#][ ].*$");

	void formatList() {
		throw Lang.makeThrow("No implement yet!");
	}

}
