package org.nutz.doc.plain;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nutz.doc.Code;
import org.nutz.doc.FinalLine;
import org.nutz.doc.HorizontalLine;
import org.nutz.doc.Line;
import org.nutz.doc.Doc;
import org.nutz.doc.DocParser;
import org.nutz.doc.Inline;
import org.nutz.doc.Media;
import org.nutz.doc.OrderedListItem;
import org.nutz.doc.Refer;
import org.nutz.doc.UnorderedListItem;
import org.nutz.doc.ZRow;
import org.nutz.doc.style.FontStyle;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;

public class PlainParser implements DocParser {

	@Override
	public Doc parse(File src) {
		InputStream ins = null;
		try {
			ins = Streams.fileIn(src);
			/*
			 * Prepare the reader
			 */
			BufferedReader br;
			try {
				br = new BufferedReader(new InputStreamReader(ins, "UTF-8"));
			} catch (UnsupportedEncodingException e1) {
				throw Lang.wrapThrow(e1);
			}
			/*
			 * Parepare document
			 */
			Doc doc = new Doc();
			String line;
			LineWrapper root = new LineWrapper();
			root.line = doc.root();
			root.deep = -1;
			/*
			 * Parse each line
			 */
			List<LineWrapper> lineList = Doc.LIST(LineWrapper.class);
			try {
				while (null != (line = br.readLine())) {
					LineWrapper lw = parseLine(br, doc, line);
					if (lw.line == null)
						continue;
					lineList.add(lw);
				}
			} catch (IOException e) {
				throw Lang.wrapThrow(e);
			}
			LineWrapper[] lines = lineList.toArray(new LineWrapper[lineList.size()]);
			/*
			 * Make tree
			 */
			for (int i = 0; i < lines.length; i++) {
				LineWrapper bw = lines[i];
				LineWrapper prev = root;
				for (int x = i - 1; x >= 0; x--) {
					if (lines[x].line.is(RootLine.class))
						continue;
					if (lines[x].line.is(FinalLine.class))
						continue;
					prev = lines[x];
					break;
				}
				if (bw.line.is(Line.class) && bw.line.isBlank()) {
					// check next un-blank line is child of pre-line
					LineWrapper next = null;
					int j = i + 1;
					for (; j < lines.length; j++)
						if (!lines[j].line.is(Line.class) || !lines[j].line.isBlank()) {
							next = lines[j];
							break;
						}
					if (null == next)
						break;
					if (next.deep > prev.deep) {
						prev.line.addChild(next.line);
					} else {
						Line parent = prev.line;
						while (parent.depth() >= next.deep) {
							parent = parent.parent();
						}
						parent.addChild(bw.line);
						parent.addChild(next.line);
					}
					i = j;
				} else {
					Line parent = prev.line;
					while (parent.depth() >= bw.deep) {
						parent = parent.parent();
					}
					if (bw.line instanceof RootLine) {
						for (Iterator<Line> it = ((RootLine) bw.line).root.childIterator(); it.hasNext();)
							parent.addChild(it.next());

					} else {
						parent.addChild(bw.line);
					}
				}
			}
			return doc;
		} finally {
			if (null != ins)
				try {
					ins.close();
				} catch (IOException e) {
					throw Lang.wrapThrow(e);
				}
		}
	}

	private static class LineWrapper {
		Line line;
		int deep;

		@Override
		public String toString() {
			return String.format("%d:\t%s", deep, line.toString());
		}

	}

	private static class RootLine extends Line {
		private Line root;

		RootLine(Line root) {
			this.root = root;
		}
	}

	private LineWrapper parseLine(BufferedReader reader, Doc doc, String line) {
		LineWrapper lw = new LineWrapper();
		char[] cs = line.toCharArray();
		for (; lw.deep < cs.length; lw.deep++)
			if (cs[lw.deep] != '\t')
				break;
		String s = new String(cs, lw.deep, cs.length - lw.deep);
		if (s.matches(HR))
			lw.line = new HorizontalLine();
		else
			lw.line = parseLine2(reader, doc, lw.deep, s);
		return lw;
	}

	private static Pattern ROW = Pattern.compile("(?<=[|]{2})([^|]*)(?=[|]{2})");
	private static Pattern INCLUDE = Pattern.compile("^@include:", Pattern.CASE_INSENSITIVE);
	private static Pattern CODESTART = Pattern.compile("^([{]{3})(<[a-zA-Z]+>)?");
	private static Pattern CODEEND = Pattern.compile("[}]{3}$");
	private static Pattern INDEXTABLE = Pattern.compile("^(#index:)(([0-9],)?[0-9])$", Pattern.CASE_INSENSITIVE);
	private static Pattern DOC_TITLE = Pattern.compile("^(#title:)(.*)$", Pattern.CASE_INSENSITIVE);
	private static Pattern DOC_AUTHOR = Pattern.compile("^(#author:)(.*)$", Pattern.CASE_INSENSITIVE);
	private static Pattern DOC_VIRIFIER = Pattern.compile("^(#verifier:)(.*)$", Pattern.CASE_INSENSITIVE);
	private static Pattern OL = Pattern.compile("^([\\s]*[#][\\s]+)(.*)$");
	private static Pattern UL = Pattern.compile("^([\\s]*[*][\\s]+)(.*)$");
	private static String HR = "^[\\s]*-{5,}[\\s]*$";

	private Line parseLine2(BufferedReader reader, Doc doc, int deep, String s) {
		Matcher matcher;
		String ss = Strings.trim(s);
		/*
		 * Set document title
		 */
		matcher = DOC_TITLE.matcher(ss);
		if (matcher.find()) {
			doc.setTitle(matcher.group(2));
			return null;
		}
		matcher = DOC_AUTHOR.matcher(ss);
		if (matcher.find()) {
			doc.addAuthor(matcher.group(2));
			return null;
		}
		matcher = DOC_VIRIFIER.matcher(ss);
		if (matcher.find()) {
			doc.addVerifier(matcher.group(2));
			return null;
		}
		/*
		 * The line is row
		 */
		if (ss.startsWith("||") && ss.endsWith("||")) {
			ZRow row = Doc.row();
			Matcher m = ROW.matcher(ss);
			while (m.find()) {
				String sln = m.group();
				LineWrapper lw = parseLine(reader, doc, sln);
				row.addChild(lw.line);
			}
			return row;
		}
		/*
		 * The line is for include something
		 */
		matcher = INCLUDE.matcher(s);
		if (matcher.find()) {
			String rs = Strings.trim(s.substring(matcher.end()));
			Refer re = Doc.refer(doc, rs);
			if (null == re.getFile() || !re.getFile().exists()) {
				throw Lang.makeThrow("Fail to find doc file '%s'!!!", re.getFile().getAbsolutePath());
			}
			Doc doc2 = this.parse(re.getFile());
			return new RootLine(doc2.root());
		}
		/*
		 * The line is for code zzh: the real code should not appear in same
		 * line of the CODESTART
		 */
		if (ss.startsWith("{{{")) {
			StringBuilder sb = new StringBuilder();
			Code.TYPE type = null;
			// Get the code type
			matcher = CODESTART.matcher(ss);
			matcher.find();
			if (matcher.groupCount() == 2) {
				try {
					String tstr = matcher.group(2);
					tstr = tstr.substring(1, tstr.length() - 1);
					type = Code.TYPE.valueOf(tstr.toLowerCase());
				} catch (Exception e) {
					type = Code.TYPE.Unknown;
				}
			}
			// read line
			// and the CODEEND should not appear in the same line of real code.
			String line;
			try {
				while (null != (line = reader.readLine())) {
					if (CODEEND.matcher(line).find())
						break;
					int pos;
					for (pos = 0; pos < deep; pos++) {
						if (pos >= line.length())
							break;
						if (line.charAt(pos) != '\t')
							break;
					}
					sb.append(line.substring(pos)).append('\n');
				}
				if (sb.length() > 0)
					sb.deleteCharAt(sb.length() - 1);
			} catch (Exception e) {
				throw Lang.wrapThrow(e);
			}
			return Doc.code(sb.toString(), type);
		}
		/*
		 * The line is a index table
		 */
		matcher = INDEXTABLE.matcher(s);
		if (matcher.find()) {
			return Doc.indexTable(s.substring(s.indexOf(':') + 1));
		}
		/*
		 * The line is contains a group of text
		 */
		Class<? extends Line> lineType;
		char[] cs;
		matcher = UL.matcher(s);
		if (matcher.find()) {
			cs = matcher.group(2).toCharArray();
			lineType = UnorderedListItem.class;
		} else {
			matcher = OL.matcher(s);
			if (matcher.find()) {
				cs = matcher.group(2).toCharArray();
				lineType = OrderedListItem.class;
			} else {
				cs = s.toCharArray();
				lineType = Line.class;
			}
		}
		return Doc.line(lineType, parseInlineList(cs));
	}

	private List<Inline> parseInlineList(char[] cs) {
		List<Inline> inlines = Doc.LIST(Inline.class);
		TokenWalker tw = new TokenWalker(cs);
		tw.add('`', '`', true).add('{', '}').add('<', '>').add('[', ']');
		Token token;
		while (null != (token = tw.next())) {
			String s = token.getContent();
			switch (token.getName()) {
			case '`':
				if (s.length() == 0)
					inlines.add(Doc.inline("`"));
				else
					inlines.add(Doc.inline(s.toString()));
				break;
			case '{':
				if (s.length() > 0)
					inlines.add(toInline(s));
				break;
			case '<':
				if (s.length() > 0) {
					Media media = Doc.media(s);
					inlines.add(media);
				}
				break;
			case '[':
				if (s.length() > 0) {
					int pos = s.indexOf(' ');
					if (pos > 0) {
						String href = s.substring(0, pos);
						List<Inline> inls = parseInlineList(s.substring(pos + 1).toCharArray());
						for (Inline il : inls)
							il.href(href);
						inlines.addAll(inls);
					} else {
						Inline inln = Doc.inline(s);
						inln.href(s);
						inlines.add(inln);
					}
				}
				break;
			default:
				inlines.add(Doc.inline(token.getContent()));
			}
		}
		return inlines;
	}

	private static Pattern MARK = Pattern.compile("^(([~_*^,])|(#[0-9a-fA-F]{3,6};))*");

	private Inline toInline(String s) {
		Matcher m = MARK.matcher(s);
		if (m.find()) {
			String mark = m.group();
			Inline inline = parseInline(s.substring(mark.length()));
			char[] cs = mark.toCharArray();
			for (int i = 0; i < cs.length; i++) {
				char c = cs[i];
				switch (c) {
				case '~':
					inline.getStyle().getFont().addStyle(FontStyle.STRIKE);
					break;
				case '_':
					inline.getStyle().getFont().addStyle(FontStyle.ITALIC);
					break;
				case '*':
					inline.getStyle().getFont().addStyle(FontStyle.BOLD);
					break;
				case '^':
					inline.getStyle().getFont().setAsSup();
					break;
				case ',':
					inline.getStyle().getFont().setAsSub();
					break;
				case '#':
					int j = i + 1;
					for (; j < cs.length; j++)
						if (cs[j] == ';')
							break;
					String color = mark.substring(i, j);
					i = j;
					inline.getStyle().getFont().setColor(color);
					break;
				}
			}
			return inline;
		}
		return parseInline(s);
	}

	private static Pattern LINKS = Pattern.compile("^([\\[])(.*)([\\]])$");

	private Inline parseInline(String s) {
		Matcher m = LINKS.matcher(s);
		if (m.find()) {
			s = m.group(2);
			String[] ss = Strings.splitIgnoreBlank(s, "[ ]");
			if (ss.length == 1) {
				Media media = parseMedia(ss[0]);
				if (null != media)
					return media;
				Inline inline = Doc.inline(ss[0]);
				inline.href(ss[0]);
				return inline;
			} else {
				String txt = Lang.concatBy(1, ss.length - 1, ' ', ss).toString();
				Inline inline = Doc.inline(txt);
				inline.href(ss[0]);
				return inline;
			}
		}
		return Doc.inline(s);
	}

	private static Pattern MEDIAS = Pattern.compile(
			"^([/\\\\]|[a-zA-Z]:[/\\\\])?([a-zA-Z0-9_/\\\\])*([.](png|gif|jpeg|jpg))$", Pattern.CASE_INSENSITIVE);

	private Media parseMedia(String s) {
		if (MEDIAS.matcher(s).find()) {
			Media m = Doc.media(s);
			m.setText(s);
			return m;
		}
		return null;
	}

}
