package org.nutz.doc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.nutz.lang.Lang;

public class Doc {

	public static <T> List<T> LIST(Class<T> type) {
		return new ArrayList<T>();
	}

	public static Inline inline(String text) {
		Inline i = new Inline();
		i.setText(text);
		return i;
	}

	public static <T extends Line> Line line(Class<T> type, List<Inline> eles) {
		try {
			Line b = type.newInstance();
			for (Inline e : eles)
				b.append(e);
			return b;
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
	}

	public static Line line(Inline ele) {
		return new Line().append(ele);
	}

	public static Line line(String text) {
		return line(inline(text));
	}

	public static Including including(String s) {
		return including(s);
	}

	public static Including including(Refer refer, DocParser parser) {
		Including inc = new Including();
		inc.setRefer(refer);
		inc.setParser(parser);
		return inc;
	}

	public static Refer refer(String str) {
		return new Refer(str);
	}

	public static Media media(String src) {
		Media m = new Media();
		m.src(src);
		return m;
	}

	public static Code code(String text, Code.TYPE type) {
		Code code = new Code();
		code.setType(null == type ? Code.TYPE.Unknown : type);
		code.setText(text);
		return code;
	}

	public static IndexTable indexTable(int level) {
		IndexTable it = new IndexTable();
		it.setLevel(level);
		return it;
	}

	/*-----------------------------------------------------------------*/

	public Doc() {
		root = new Line();
		root.setDoc(this);
	}

	private Line root;
	private String title;
	private String subTitle;
	private String author;
	private String lastModify;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSubTitle() {
		return subTitle;
	}

	public void setSubTitle(String sutTitle) {
		this.subTitle = sutTitle;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getLastModify() {
		return lastModify;
	}

	public void setLastModify(String lastModify) {
		this.lastModify = lastModify;
	}

	public Line root() {
		return root;
	}

	public Line getIndex(int level) {
		return getIndex(root, level);
	}

	private static Line getIndex(Line line, int level) {
		if (line instanceof FinalLine || !line.isHeading())
			return null;
		Line root = Doc.line(line.getText());
		root.id = line.id;
		if (level > 0) {
			for (Iterator<Line> it = line.childIterator(); it.hasNext();) {
				Line indxtab = getIndex(it.next(), level - 1);
				if (null != indxtab)
					root.addChild(indxtab);
			}
		}
		return root;
	}

	public <T extends Line> boolean contains(Class<T> type) {
		return root.contains(type);
	}
}
