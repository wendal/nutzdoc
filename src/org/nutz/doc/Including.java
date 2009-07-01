package org.nutz.doc;

import java.io.IOException;
import java.io.Reader;

import org.nutz.lang.Lang;
import org.nutz.lang.Streams;

public class Including extends FinalLine {

	private DocParser parser;

	private Refer refer;

	public Refer getRefer() {
		return refer;
	}

	public void setRefer(Refer refer) {
		this.refer = refer;
	}

	public void setParser(DocParser parser) {
		this.parser = parser;
	}

	public Doc getDoc() {
		Doc doc;
		try {
			Reader r = Streams.fileInr(refer.getFile());
			doc = parser.parse(r);
			r.close();
		} catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
		return doc;
	}

	@Override
	public String toString() {
		return String.format("@>include: %s", refer.toString());
	}
}
