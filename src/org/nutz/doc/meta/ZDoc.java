package org.nutz.doc.meta;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.nutz.lang.util.IntRange;

public class ZDoc {

	public ZDoc() {
		root = new ZBlock().setDoc(this);
		last = root;
		authors = new LinkedList<Author>();
		verifiers = new LinkedList<Author>();
	}

	private List<Author> authors;
	private List<Author> verifiers;
	private File source;
	private ZBlock root;
	private ZBlock last;

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

	public ZBlock last() {
		return last;
	}

	public void setLast(ZBlock last) {
		this.last = last;
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

	public Author[] verifiers() {
		return verifiers.toArray(new Author[verifiers.size()]);
	}

	public ZBlock buildIndex(IntRange range) {
		ZBlock re = ZDocs.p();
		_buildIndex(re, range, root);
		return re;
	}

	private static void _buildIndex(ZBlock re, IntRange range, ZBlock me) {
		int lvl = me.depth() - 1;
		ZBlock myre = ZDocs.p(me.getText());
		if (range.inon(lvl)) {
			re.add(myre);
		}
		if (!range.lt(lvl)) {
			for (ZBlock p : me.children())
				_buildIndex(myre, range, p);
		}

	}
}
