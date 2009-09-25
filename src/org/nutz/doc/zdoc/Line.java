package org.nutz.doc.zdoc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nutz.doc.meta.Author;
import org.nutz.doc.meta.ZBlock;
import org.nutz.doc.meta.ZDocs;
import org.nutz.doc.meta.ZType;
import org.nutz.lang.Strings;
import org.nutz.lang.util.IntRange;

class Line {

	private static final Pattern CODE_START = Pattern.compile("^([{]{3})([ \t]*)(<.*>)?([ \t]*)$");
	private static final Pattern CODE_END = Pattern.compile("^[}]{3}$");
	private static final Pattern ROW = Pattern.compile("^[|][|].+[|][|]$");
	private static final Pattern UL = Pattern.compile("^([*][ ])(.*)$");
	private static final Pattern OL = Pattern.compile("^([#][ ])(.*)$");
	private static final Pattern HR = Pattern.compile("^[-]{5,}$");
	private static final Pattern VERIFIER = Pattern.compile("^([#]verifier:)(.*)$");
	private static final Pattern AUTHOR = Pattern.compile("^([#]author:)(.*)$");
	private static final Pattern INDEX_RANGE = Pattern.compile("^([#]index:)([0-9]+[,:][0-9]+)([ \t]*)$");

	static Line make(Line parent, String text) {
		return new Line(parent, text);
	}

	ZType type;
	private String codeType;
	private Line parent;
	private String text;
	private List<Line> children;
	private int depth;
	private boolean codeEnd;
	private boolean endByEscape;
	private IntRange indexRange;
	private String title;
	private Author author;
	private Author verifier;

	private Line(Line parent, String txt) {
		this.parent = parent;
		if (null != parent) {
			parent.children.add(this);
			depth = parent.depth + 1;
		}
		this.text = Strings.trim(txt);
		children = new ArrayList<Line>();
		// Tiltle
		Matcher m = Pattern.compile("^([#]title:)(.*)$").matcher(text);
		if (m.find()) {
			title = m.group(2);
			return;
		}
		// Index Range
		m = INDEX_RANGE.matcher(text);
		if (m.find()) {
			indexRange = IntRange.make(m.group(2));
			return;
		}
		// Author
		m = AUTHOR.matcher(text);
		if (m.find()) {
			author = ZDocs.author(m.group(2));
			return;
		}
		// Verifier
		m = VERIFIER.matcher(text);
		if (m.find()) {
			verifier = ZDocs.author(m.group(2));
			return;
		}
		// HR
		m = HR.matcher(text);
		if (m.find()) {
			type = ZType.HR;
			return;
		}
		// Code start
		m = CODE_START.matcher(text);
		if (m.find()) {
			String s = m.group(3);
			if (null == s) {
				codeType = "";
			}
			codeType = s.substring(1, s.length() - 1);
			return;
		}
		// Code end
		m = CODE_END.matcher(text);
		if (m.find()) {
			codeEnd = true;
			return;
		}
		evalMode();
	}

	private void evalMode() {
		// End by escape
		endByEscape = (text.length() > 0)
				&& ((text.length() == 1 && text.charAt(0) == '\\') || (text.matches("^.*[^\\\\][\\\\]$")));
		// OL
		Matcher m = OL.matcher(text);
		if (m.find()) {
			type = ZType.OLI;
			text = m.group(2);
			return;
		}
		// UL
		m = UL.matcher(text);
		if (m.find()) {
			type = ZType.ULI;
			text = m.group(2);
			return;
		}
		// Row
		m = ROW.matcher(text);
		if (m.find()) {
			type = ZType.ROW;
			return;
		}
	}

	Line getParent() {
		return parent;
	}

	Line setParent(Line parent) {
		this.parent = parent;
		return this;
	}

	String getText() {
		return text;
	}

	Line setText(String text) {
		this.text = text;
		return this;
	}

	Iterator<Line> it() {
		return children.iterator();
	}

	boolean hasChild() {
		return children.size() > 0;
	}

	boolean withoutChild() {
		return children.isEmpty();
	}

	int depth() {
		return depth;
	}

	boolean isHr() {
		return ZType.HR == type;
	}

	boolean isOLI() {
		return ZType.OLI == type;
	}

	boolean isCodeStart() {
		return null != codeType;
	}

	boolean isULI() {
		return ZType.ULI == type;
	}

	boolean isRow() {
		return ZType.ROW == type;
	}

	boolean isNormal() {
		return null == type;
	}

	String getCodeType() {
		return codeType;
	}

	boolean isCodeEnd() {
		return codeEnd;
	}

	boolean isEndByEscape() {
		return endByEscape;
	}

	IntRange getIndexRange() {
		return indexRange;
	}

	String getTitle() {
		return title;
	}

	Author getAuthor() {
		return author;
	}

	Author getVerifier() {
		return verifier;
	}

	boolean isBlank() {
		return Strings.isBlank(text);
	}

	void join(Line line) {
		if (null != line) {
			text = text + line.text;
			evalMode();
		}
	}

	ZBlock toBlock() {
		return Parsing.toBlock(text.toCharArray());
	}

	public String toString() {
		return toString(0);
	}

	public String toString(int depth) {
		StringBuilder sb = new StringBuilder();
		if (codeType == null)
			sb.append(Strings.dup('\t', depth)).append(text);
		Iterator<Line> it = children.iterator();
		while (it.hasNext())
			sb.append(toString(depth + 1));
		return sb.toString();
	}

}
