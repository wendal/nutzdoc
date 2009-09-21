package org.nutz.doc.text;

import java.io.File;
import java.io.IOException;

import org.nutz.doc.DocParser;
import org.nutz.doc.meta.ZDoc;

public class TextFileParser implements DocParser {

	@Override
	public ZDoc parse(File src) throws IOException {
		TextParsing parsing = new TextParsing(src);
		parsing.open();
		parsing.process();
		parsing.close();
		return parsing.getDoc();
	}

}
