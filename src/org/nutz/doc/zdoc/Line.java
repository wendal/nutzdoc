package org.nutz.doc.zdoc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nutz.doc.meta.ZType;
import org.nutz.lang.Strings;
import org.nutz.lang.util.IntRange;

class Line {

	private static final Pattern CODE_START = Pattern.compile("^([{]{3})([ \t]*)(<.*>)?([ \t]*)$");
	private static final Pattern ROW = Pattern.compile("^[|][|].+[|][|]$");
	private static final Pattern UL = Pattern.compile("^([*][ ])(.*)$");
	private static final Pattern OL = Pattern.compile("^([#][ ])(.*)$");
	private static final Pattern HR = Pattern.compile("^([-]{5,})$");

	ZType type;
	private String codeType;
	private Line parent;
	private Line prev;
	private String text;
	private List<Line> children;
	private int depth;
	private IntRange indexRange;

	/**
	 * 构造文档根行
	 * 
	 * @param txt
	 */
	Line() {
		children = new ArrayList<Line>();
	}

	/**
	 * 构造一般的行以及空行
	 * 
	 * @param txt
	 */
	Line(String txt) {
		if (null != txt) {
			this.text = null == txt ? "" : txt;
			children = new ArrayList<Line>();
			evalMode();
		}
	}

	/**
	 * 构造 IndexRange
	 */
	Line(IntRange range) {
		this.indexRange = range;
		text = range.toString();
	}

	private void evalMode() {
		String trim = Strings.trim(this.text);
		// OL
		Matcher m = OL.matcher(trim);
		if (m.find()) {
			type = ZType.OLI;
			text = m.group(2);
			return;
		}
		// UL
		m = UL.matcher(trim);
		if (m.find()) {
			type = ZType.ULI;
			text = m.group(2);
			return;
		}
		// Row
		m = ROW.matcher(trim);
		if (m.find()) {
			type = ZType.ROW;
			return;
		}
		// HR
		m = HR.matcher(trim);
		if (m.find()) {
			type = ZType.HR;
			return;
		}
		// Code start
		m = CODE_START.matcher(trim);
		if (m.find()) {
			String s = m.group(3);
			if (null == s) {
				codeType = "";
			} else
				codeType = s.substring(1, s.length() - 1);
			type = ZType.CODE;
			return;
		}
	}

	Line getParent() {
		return parent;
	}

	Line getPrev() {
		return prev;
	}

	String getText() {
		return text;
	}

	void setText(String text) {
		this.text = text;
	}

	Line add(Line line) {
		line.parent = this;
		if (children.size() > 0) {
			line.prev = children.get(children.size() - 1);
		}
		children.add(line);
		line.depth = depth + 1;
		return this;
	}

	List<Line> children() {
		return children;
	}

	Line child(int... indexes) {
		Line me = this;
		for (int i : indexes)
			me = me.children.get(i);
		return me;
	}

	boolean hasChild() {
		return children.size() > 0;
	}

	boolean withoutChild() {
		return null == children || children.isEmpty();
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

	IntRange getIndexRange() {
		return indexRange;
	}

	boolean isBlank() {
		return Strings.isBlank(text);
	}

	void join(String str) {
		if (null != str) {
			text = text + str;
			evalMode();
		}
	}

	char[] getCharArray() {
		return text.toCharArray();
	}

	public String toString() {
		return toString(null != parent ? 0 : -1);
	}

	public String toString(int depth) {
		StringBuilder sb = new StringBuilder();
		if (null != parent)
			sb.append(Strings.dup('\t', depth));
		sb.append(symbol()).append(text == null ? "" : text).append('\n');
		sb.append(getChildrenString(depth));
		return sb.toString();
	}

	public String getChildrenString() {
		return getChildrenString(null != parent ? 0 : -1);
	}

	public String getChildrenString(int depth) {
		StringBuilder sb = new StringBuilder();
		Iterator<Line> it = children.iterator();
		while (it.hasNext())
			sb.append(it.next().toString(depth + 1));
		return sb.toString();
	}

	String symbol() {
		if (isOLI())
			return "# ";
		if (isULI())
			return "* ";
		return "";
	}

	public boolean isIndexRange() {
		return null != this.indexRange;
	}

}
