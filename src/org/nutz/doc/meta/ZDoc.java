package org.nutz.doc.meta;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class ZDoc {

	public ZDoc() {
		root = new ZBlock().setDoc(this);
		authors = new LinkedList<Author>();
		verifiers = new LinkedList<Author>();
	}

	private List<Author> authors;
	private List<Author> verifiers;
	private File source;
	private ZBlock root;

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

	public Author[] verifiers() {
		return verifiers.toArray(new Author[verifiers.size()]);
	}

}
