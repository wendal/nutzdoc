package org.nutz.doc;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

public class Doc implements DocBase {

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

	public static Refer refer(DocBase base, String str) {
		return new Refer(base, str);
	}

	private static Pattern SIZE = Pattern.compile("^([0-9]+[xX][0-9]+)(:)");

	public static Media media(String str) {
		if (null == str)
			return null;
		Matcher mat = SIZE.matcher(str);
		String src = str;
		Media m = new Media();
		if (mat.find()) {
			src = str.substring(mat.group().length());
			String size = mat.group(1);
			String[] ss = Strings.splitIgnoreBlank(size, "[xX]");
			m.width(Integer.parseInt(ss[0])).height((Integer.parseInt(ss[1])));
		}
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
		attributes = new HashMap<String, Object>();
		root = new Line();
		root.setDoc(this);
		root.setDepth(-1);
		authors = new LinkedList<Author>();
		verifiers = new LinkedList<Author>();
	}

	private Line root;
	private String title;
	private List<Author> authors;
	private List<Author> verifiers;
	private File file;
	private Map<String, Object> attributes;

	public Map<String, Object> attributes() {
		return attributes;
	}

	public File getFile() {
		return file;
	}

	@Override
	public String getAbsolutePath() {
		return null == file ? null : file.getAbsolutePath();
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

	public List<Author> getVerifiers() {
		return verifiers;
	}

	public List<Author> getAuthors() {
		return authors;
	}

	public void addVerifier(String verifier) {
		addVerifier(new Author(verifier));
	}

	public void addVerifier(Author verifier) {
		if (!verifiers.contains(verifier))
			verifiers.add(verifier);
	}

	public void addAuthor(String author) {
		addAuthor(new Author(author));
	}

	public void addAuthor(Author author) {
		if (!authors.contains(author))
			authors.add(author);
	}

	public boolean hasAuthor() {
		return authors.size() > 0;
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

	public void removeIndexTable() {
		for (Line l : root().children)
			l.removeIndexTable();
	}

	public String getRelativePath(File file) {
		File base = this.getFile();
		if (base.isFile())
			base = base.getParentFile();
		String[] bb = Strings.splitIgnoreBlank(base.getAbsolutePath(), "[\\\\/]");
		String[] ff = Strings.splitIgnoreBlank(file.getAbsolutePath(), "[\\\\/]");
		int pos = 0;
		for (; pos < Math.min(bb.length, ff.length); pos++)
			if (!bb[pos].equals(ff[pos]))
				break;
		String path = Strings.dup("../", bb.length - pos);
		path += Lang.concatBy(pos, ff.length - pos, '/', ff);
		return path;
	}
}
