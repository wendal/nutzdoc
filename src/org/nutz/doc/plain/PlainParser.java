package org.nutz.doc.plain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nutz.doc.Line;
import org.nutz.doc.Doc;
import org.nutz.doc.DocParser;
import org.nutz.doc.Document;
import org.nutz.doc.Inline;
import org.nutz.doc.style.Font;
import org.nutz.lang.Lang;
import org.nutz.lang.util.LinkedCharArray;

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
				LinekWrapper bw = parseLine(line);
				// find the parent to append
				while (b.hasParent() && b.deep() > bw.deep)
					b = b.parent();
				b.addChild(bw.line);
				b = bw.line;
			}
		} catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
		return doc;
	}

	private static class LinekWrapper {
		Line line;
		int deep;
	}

	private LinekWrapper parseLine(String line) {
		LinekWrapper lw = new LinekWrapper();
		char[] cs = line.toCharArray();
		for (; lw.deep < cs.length; lw.deep++)
			if (cs[lw.deep] != '\t')
				break;
		lw.line = Doc.line(parseInlines(new String(cs, lw.deep, cs.length
				- lw.deep)));
		return lw;
	}

	private List<Inline> parseInlines(String s) {
		List<Inline> inlines = new ArrayList<Inline>();
		LinkedCharArray lca = new LinkedCharArray();
		StringBuilder sb = new StringBuilder();
		char[] cs = s.toCharArray();
		for (char c : cs) {
			switch (c) {
			case '{':
				if (lca.last() == '{') {
					sb.append(lca.clear());
				} else {
					if (lca.size() > 0) {
						sb.append(lca.clear());
					}
					if (sb.length() > 0) {
						inlines.add(toInline(sb.toString()));
						sb = new StringBuilder();
					}
					lca.push(c);
				}
				break;
			case '}':
				if (lca.first() == '{') {
					sb.append(lca.push(c).clear());
					inlines.add(toInline(sb.toString()));
					sb = new StringBuilder();
				} else {
					lca.push(c);
				}
				break;
			default:
				lca.push(c);
			}
		}
		if (lca.size() > 0)
			sb.append(lca.clear());
		if (sb.length() > 0)
			inlines.add(toInline(sb.toString()));
		return inlines;
	}

	private static Pattern QUOTE = Pattern.compile("^([{])(.*)([}])$");
	private static Pattern MARK = Pattern.compile("^[~_*^,]*");

	private Inline toInline(String s) {
		Matcher m = QUOTE.matcher(s);
		if (m.find()) {
			s = m.group(2);
			m = MARK.matcher(s);
			if (m.find()) {
				String mark = m.group();
				Inline inline = Doc.inline(s.substring(mark.length()));
				for (char c : mark.toCharArray()) {
					switch (c) {
					case '~':
						inline.getStyle().getFont().addStyle(Font.STRIKE);
						break;
					case '_':
						inline.getStyle().getFont().addStyle(Font.ITALIC);
						break;
					case '*':
						inline.getStyle().getFont().addStyle(Font.BOLD);
						break;
					case '^':
						inline.getStyle().getFont().setAsSup();
						break;
					case ',':
						inline.getStyle().getFont().setAsSub();
						break;
					}
				}
				return inline;
			}
		}
		return Doc.inline(s);
	}

	public static void main(String[] args) {
		Matcher m = MARK.matcher("*_~SBCCC");
		m.find();
		System.out.println(m.group());
	}

}
