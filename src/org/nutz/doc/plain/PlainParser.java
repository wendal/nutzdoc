package org.nutz.doc.plain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.nutz.doc.Line;
import org.nutz.doc.Doc;
import org.nutz.doc.DocParser;
import org.nutz.doc.Document;
import org.nutz.doc.Inline;
import org.nutz.lang.Lang;

public class PlainParser implements DocParser {

	@Override
	public Document parse(Reader reader) {
		/*
		 * Prepare the reader
		 */
		BufferedReader br = null;
		if (reader instanceof BufferedReader)
			br = (BufferedReader) reader;
		else
			br = new BufferedReader(reader);
		/*
		 * Parepare document
		 */
		Document doc = new Document();
		String line;
		Line b = doc.root();
		try {
			while (null != (line = br.readLine())) {
				BlockWrapper bw = parseLine(line);
				// find the parent to append
				while (b.hasParent() && b.deep() > bw.deep)
					b = b.parent();
				b.addChild(bw.block);
				b = bw.block;
			}
		} catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
		return doc;
	}

	private static class BlockWrapper {
		Line block;
		int deep;
	}

	private BlockWrapper parseLine(String line) {
		BlockWrapper blockWrapper = new BlockWrapper();
		List<Inline> list = new ArrayList<Inline>();
		char[] cs = line.toCharArray();
		for (; blockWrapper.deep < cs.length; blockWrapper.deep++)
			if (cs[blockWrapper.deep] != '\t')
				break;
		blockWrapper.block = Doc.line(new String(cs, blockWrapper.deep, cs.length
				- blockWrapper.deep));
		return blockWrapper;
	}

}
