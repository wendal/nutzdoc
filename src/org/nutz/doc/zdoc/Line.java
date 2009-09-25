package org.nutz.doc.zdoc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nutz.doc.meta.Author;
import org.nutz.doc.meta.ZDocs;
import org.nutz.doc.meta.ZType;
import org.nutz.lang.Strings;
import org.nutz.lang.util.IntRange;

class Line {

	static Line make(Line parent, String text) {
		return new Line(parent, text);
	}

	private ZType type;
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

	private Line(Line parent, String text) {
		this.parent = parent;
		if (null != parent) {
			parent.children.add(this);
			depth = parent.depth + 1;
		}
		this.text = text;
		children = new ArrayList<Line>();
		// Tiltle
		Matcher m = Pattern.compile("^([#]title:)(.*)$").matcher(text);
		if (m.find())
			title = m.group(2);
		// Author
		m = Pattern.compile("^([#]author:)(.*)$").matcher(text);
		if (m.find())
			author = ZDocs.author(m.group(2));
		// Verifier
		m = Pattern.compile("^([#]verifier:)(.*)$").matcher(text);
		if (m.find())
			verifier = ZDocs.author(m.group(2));

		// Code type
		m = Pattern.compile("^([{]{3})([ \t]*)(<.*>)?([ \t]*)$").matcher(text);
		if (m.find()) {
			String s = m.group(3);
			if (null == s) {
				codeType = "";
			}
			codeType = s.substring(1, s.length() - 1);
		}
		// OL | UL | Row
		if (text.matches("^[ ]*[#][ ].*$"))
			type = ZType.OL;
		else if (text.matches("^[ ]*[*][ ].*$"))
			type = ZType.UL;
		else if (text.matches("^[|][|].+[|][|][ \t]*$"))
			type = ZType.ROW;
		else if (text.matches("^[ \t]*[}]{3}[ \t]*$"))
			codeEnd = true;

		// End by escape
		endByEscape = (text.length() > 0)
				&& ((text.length() == 1 && text.charAt(0) == '\\') || (text
						.matches("^.*[^\\\\][\\\\]$")));
		// Index Range
		m = Pattern.compile("^([#]index:)([0-9]+[,:][0-9]+)([ \t]*)$").matcher(text);
		if (m.find())
			indexRange = IntRange.make(m.group(2));

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

	int depth() {
		return depth;
	}

	boolean isOL() {
		return ZType.OL == type;
	}

	boolean isUL() {
		return ZType.UL == type;
	}

	boolean isRow() {
		return ZType.ROW == type;
	}

	boolean isSameTypeWith(Line line) {
		if (isOL())
			return line.isOL();
		if (isUL())
			return line.isUL();
		return true;
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

}
