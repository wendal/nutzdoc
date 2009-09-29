package org.nutz.doc.meta;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

public class ZDoc {

	public ZDoc() {
		root = new ZBlock().setDoc(this);
		authors = new LinkedList<Author>();
		verifiers = new LinkedList<Author>();
		attrs = new HashMap<String, Object>();
		ids = new HashMap<Object, Integer>();
	}

	private int _ID_;
	private List<Author> authors;
	private List<Author> verifiers;
	private File source;
	private ZBlock root;
	private Map<String, Object> attrs;
	private Map<Object, Integer> ids;

	int getId(Object key) {
		Integer id = ids.get(key);
		if (null == id) {
			id = _ID_++;
			ids.put(key, id);
		}
		return id;
	}

	public Object getAttr(String name) {
		return attrs.get(name);
	}

	public boolean hasAttr(String name) {
		return attrs.containsKey(name);
	}

	public ZDoc setAttr(String name, Object value) {
		attrs.put(name, value);
		return this;
	}

	public String getTitle() {
		return root.getText();
	}

	public ZDoc setTitle(String title) {
		root.setText(title);
		return this;
	}

	public File getSource() {
		return source;
	}

	public ZDoc setSource(File source) {
		this.source = source;
		return this;
	}

	public ZBlock root() {
		return root;
	}

	public ZDoc addAuthor(Author author) {
		this.authors.add(author);
		return this;
	}

	public ZDoc addVerifier(Author verifier) {
		this.verifiers.add(verifier);
		return this;
	}

	public Author[] authors() {
		return authors.toArray(new Author[authors.size()]);
	}

	public boolean hasAuthor() {
		return !authors.isEmpty();
	}

	public boolean hasVierifier() {
		return !verifiers.isEmpty();
	}

	public Author[] verifiers() {
		return verifiers.toArray(new Author[verifiers.size()]);
	}

	public String getRelativePath(File file) {
		File base = this.getSource();
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
