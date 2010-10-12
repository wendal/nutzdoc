package org.nutz.doc.zdoc;

import java.io.BufferedReader;
import java.io.Reader;

import org.nutz.doc.DocParser;
import org.nutz.doc.meta.ZDoc;
import org.nutz.lang.Streams;

public class ZDocParser implements DocParser {

	/**
	 * 一个 \t 相当于几个空格
	 */
	private int tabpar;

	public ZDocParser() {
		this(4);
	}

	public ZDocParser(int tabpar) {
		this.tabpar = tabpar;
	}

	public ZDoc parse(Reader reader) {
		BufferedReader br = reader instanceof BufferedReader ? (BufferedReader) reader
															: new BufferedReader(reader);
		Parsing parsing = new Parsing(br);
		try {
			ZDoc doc = parsing.parse(tabpar);
			return doc;
		}
		finally {
			Streams.safeClose(br);
		}
	}

}
