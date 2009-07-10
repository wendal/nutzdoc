package org.nutz.doc;

import java.io.File;
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

	public static Line line(List<Inline> eles) {
		Line l = new Line();
		for (Inline ele : eles)
			l.append(ele);
		return l;
	}

	public static Line line(String text) {
		return line(inline(text));
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

	public static ZRow row() {
		return new ZRow();
	}

	/*-----------------------------------------------------------------*/

	public Doc() {
		root = new Line();
		root.setDoc(this);
		root.setDepth(-1);
	}

	private Line root;
	private String title;
	private String author;
	private File file;

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file.getAbsoluteFile();
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public long lastModified() {
		return file.lastModified();
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
		if (indxt.atLeft(line.depth()))
			return;
		if (indxt.isin(line.depth())) {
			Line io = Doc.line(line.getText());
			io.id = line.id;
			indexParent.addChild(io);
			indexParent = io;
		}
		for (Iterator<Line> it = line.childIterator(); it.hasNext();) {
			Line next = it.next();
			attachIndex(indexParent, next, indxt);
		}
	}

	public <T extends Line> boolean contains(Class<T> type) {
		return root.contains(type);
	}

	public List<Media> getMedias() {
		List<Media> medias = Doc.LIST(Media.class);
		for (Line l : root().children)
			medias.addAll(l.getMedias());
		return medias;
	}
	
	public void removeIndexTable(){
		for (Line l : root().children)
			l.removeIndexTable();
	}
}
