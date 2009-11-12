package org.nutz.doc.zdoc;

import java.io.BufferedReader;

import org.nutz.doc.DocParser;
import org.nutz.doc.meta.ZDoc;
import org.nutz.lang.Lang;

public class ZDocParser implements DocParser {

	public ZDoc parse(CharSequence cs) {
		BufferedReader br = new BufferedReader(Lang.inr(cs));
		Parsing parsing = new Parsing(br);
		ZDoc doc = parsing.parse();
		try {
			br.close();
		} catch (Exception e) {}
		return doc;
	}

}
