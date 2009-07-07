package org.nutz.doc;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

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

	public static IndexTable indexTable(String s) {
		return new IndexTable(s);
	}
	
	public static ZRow row(){
		return new ZRow();
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
	private String docPath;

	public String getDocPath() {
		return docPath;
	}

	public void setDocPath(String docPath) {
		this.docPath = docPath;
	}

	public String getTitle() {
		if (Strings.isBlank(title))
			return "Untitled";
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

	public Line getIndex(IndexTable idxt) {
		Line ir = Doc.line((String) null);
		attachIndex(ir, root, idxt);
		return ir;
	}

	private static void attachIndex(Line indexParent, Line line, IndexTable indxt) {
		if (line instanceof FinalLine || !line.isHeading())
			return;
		if (indxt.atLeft(line.deep()))
			return;
		if (indxt.isin(line.deep())) {
			Line io = Doc.line(line.getText());
			io.id = line.id;
			indexParent.addChild(io);
			indexParent = io;
		}
		for (Iterator<Line> it = line.childIterator(); it.hasNext();) {
			attachIndex(indexParent,it.next(),indxt);
		}
	}

	public <T extends Line> boolean contains(Class<T> type) {
		return root.contains(type);
	}
}
