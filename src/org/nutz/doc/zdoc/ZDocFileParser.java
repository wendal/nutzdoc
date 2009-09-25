package org.nutz.doc.zdoc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;

import org.nutz.doc.DocParser;
import org.nutz.doc.meta.ZDoc;
import org.nutz.lang.Streams;

public class ZDocFileParser implements DocParser {

	public ZDoc parse(File src) throws IOException {
		BufferedReader br = new BufferedReader(Streams.fileInr(src));
		Parsing parsing = new Parsing(br);
		parsing.parse();
		try {
			br.close();
		} catch (Exception e) {}
		return parsing.getDoc();
	}

}
