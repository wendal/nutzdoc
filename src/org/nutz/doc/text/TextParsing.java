package org.nutz.doc.text;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;

import org.nutz.doc.meta.ZDoc;
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

	TextParsing(File src) {
		this.src = src;
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
		c = (char) reader.read();
		return c;
	}

	void process() throws IOException {
		WorkingStack stack = new WorkingStack();
		while (stack.accept(next())) {}
		doc = (ZDoc) stack.getResult();
	}

}
