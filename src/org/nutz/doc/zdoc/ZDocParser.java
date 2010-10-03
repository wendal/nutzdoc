package org.nutz.doc.zdoc;

import java.io.BufferedReader;

import org.nutz.doc.DocParser;
import org.nutz.doc.meta.ZDoc;
import org.nutz.lang.Lang;

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

	public ZDoc parse(CharSequence cs) {
		BufferedReader br = new BufferedReader(Lang.inr(cs));
		Parsing parsing = new Parsing(br);
		ZDoc doc = parsing.parse(tabpar);
		try {
			br.close();
		}
		catch (Exception e) {}
		return doc;
	}

}
