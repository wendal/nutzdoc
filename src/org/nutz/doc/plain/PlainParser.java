package org.nutz.doc.plain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nutz.doc.Code;
import org.nutz.doc.DocRender;
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
import org.nutz.doc.html.HtmlDocRender;
import org.nutz.doc.style.FontStyle;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;

public class PlainParser implements DocParser {

	@Override
	public Doc parse(InputStream ins) {
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
		Line b = doc.root();
		try {
			while (null != (line = br.readLine())) {
				LineWrapper bw = parseLine(br, line);
				if (!(bw.line instanceof RootLine))
					if (!(bw.line instanceof FinalLine))
						if (bw.line.isBlank()) {
							if (b.hasParent() || (b.hasParent() && b.isBlank())) {
								b = b.parent();
  							}
							b.addChild(bw.line);
							b = bw.line;
							continue;
						}
				// find the parent to append
				while (b.hasParent() && b.deep() > bw.deep) {
					b = b.parent();
				}
				if (bw.line instanceof RootLine) {
					for (Iterator<Line> it = ((RootLine) bw.line).root.childIterator(); it
							.hasNext();) {
						b.addChild(it.next());
					}
				} else {
					if (b.deep() < bw.deep)
						bw.line.insert(Strings.dup('\t', bw.deep - b.deep()));
					b.addChild(bw.line);
					b = bw.line;
				}
			}
		} catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
		return doc;
	}

	private static class LineWrapper {
		Line line;
		int deep;
	}

	private static class RootLine extends Line {
		private Line root;

		RootLine(Line root) {
			this.root = root;
		}
	}

	private LineWrapper parseLine(BufferedReader reader, String line) {
		LineWrapper lw = new LineWrapper();
		char[] cs = line.toCharArray();
		for (; lw.deep < cs.length; lw.deep++)
			if (cs[lw.deep] != '\t')
				break;
		String s = new String(cs, lw.deep, cs.length - lw.deep);
		if (s.matches("^={5,}$"))
			lw.line = new HorizontalLine();
		else
			lw.line = parseLine2(reader, lw.deep, s);
		return lw;
	}

	private static Pattern ROW = Pattern.compile("(?<=[|]{2})([^|]*)(?=[|]{2})");
	private static Pattern INCLUDE = Pattern.compile("^@[>]?include:", Pattern.CASE_INSENSITIVE);
	private static Pattern CODESTART = Pattern.compile("^([{]{3})(<[a-zA-Z]+>)?");
	private static Pattern CODEEND = Pattern.compile("[}]{3}$");
	private static Pattern INDEXTABLE = Pattern.compile("^(#index:)(([0-9]:)?[0-9])",
			Pattern.CASE_INSENSITIVE);
	private static Pattern OL = Pattern.compile("^(#[\\s]+)(.*)$");
	private static Pattern UL = Pattern.compile("^([*][\\s]+)(.*)$");

	private Line parseLine2(BufferedReader reader, int deep, String s) {
		String ss = Strings.trim(s);
		/*
		 * The line is row
		 */
		if (ss.startsWith("||") && ss.endsWith("||")) {
			ZRow row = Doc.row();
			Matcher m = ROW.matcher(ss);
			while (m.find()) {
				String sln = m.group();
				LineWrapper lw = parseLine(reader, sln);
				row.addChild(lw.line);
			}
			return row;
		}
		/*
		 * The line is for include something
		 */
		Matcher matcher = INCLUDE.matcher(s);
		if (matcher.find()) {
			String rs = Strings.trim(s.substring(matcher.end()));
			Refer re = Doc.refer(rs);
			if (null == re.getFile() || !re.getFile().exists()) {
				throw Lang.makeThrow("Fail to find doc file '%s'!!!", re.getFile()
						.getAbsolutePath());
			}
			if (s.startsWith("@>")) {
				return Doc.including(re, this);
			} else {
				try {
					InputStream ins = Streams.fileIn(re.getFile());
					Doc doc = this.parse(ins);
					ins.close();
					return new RootLine(doc.root());
				} catch (IOException e) {
					throw Lang.wrapThrow(e);
				}
			}
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
					for (pos = 0; pos < deep; pos++)
						if (line.charAt(pos) != '\t')
							break;
					sb.append(line.substring(pos)).append('\n');
				}
				if (sb.length() > 0)
					sb.deleteCharAt(sb.length() - 1);
			} catch (IOException e) {
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
		return Doc.line(lineType, parseInlines(cs));
	}

	private List<Inline> parseInlines(char[] cs) {
		List<Inline> inlines = Doc.LIST(Inline.class);
		TokenWalker tw = new TokenWalker(cs);
		tw.add('`', '`').add('{', '}').add('<', '>').add('[', ']');
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
					inlines.add(toInline("{" + s + "}"));
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
						List<Inline> inls = parseInlines(s.substring(pos + 1).toCharArray());
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

	private static Pattern QUOTE = Pattern.compile("^([{])(.*)([}])$");
	private static Pattern MARK = Pattern.compile("^[~_*^,]*");

	private Inline toInline(String s) {
		Matcher m = QUOTE.matcher(s);
		if (m.find()) {
			s = m.group(2);
			m = MARK.matcher(s);
			if (m.find()) {
				String mark = m.group();
				Inline inline = parseInline(s.substring(mark.length()));
				for (char c : mark.toCharArray()) {
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
					}
				}
				return inline;
			}
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
			"^([/\\\\]|[a-zA-Z]:[/\\\\])?([a-zA-Z0-9_/\\\\])*([.](png|gif|jpeg|jpg))$",
			Pattern.CASE_INSENSITIVE);

	private Media parseMedia(String s) {
		if (MEDIAS.matcher(s).find()) {
			Media m = Doc.media(s);
			m.setText(s);
			return m;
		}
		return null;
	}

	public static void main(String[] args) {
		String s = "||A11||A12||";
		s += "\n||A21||A22||";
		DocParser parser = new PlainParser();
		Doc doc = parser.parse(Lang.ins(s));
		DocRender render = new HtmlDocRender();
		StringBuilder sb = new StringBuilder();
		render.render(Lang.ops(sb), doc);
		System.out.println(sb);
	}
}
