package org.nutz.doc.zdoc;

import java.io.BufferedReader;
import java.io.Reader;

import org.nutz.doc.DocParser;
import org.nutz.doc.meta.ZDoc;
import org.nutz.lang.Streams;
import org.nutz.lang.util.Context;

public class ZDocParser implements DocParser {

	/**
	 * 一个 \t 相当于几个空格
	 */
	private int tabpar;

	private Context context;

	public ZDocParser(Context context) {
		this(4);
		this.context = context;
	}

	public ZDocParser(int tabpar) {
		this.tabpar = tabpar;
	}

	public ZDoc parse(Reader reader) {
		BufferedReader br = reader instanceof BufferedReader ? (BufferedReader) reader
															: new BufferedReader(reader);
		Parsing parsing = new Parsing(br, context.clone());
		try {
			ZDoc doc = parsing.parse(tabpar);
			return doc;
		}
		finally {
			Streams.safeClose(br);
		}
	}

}
