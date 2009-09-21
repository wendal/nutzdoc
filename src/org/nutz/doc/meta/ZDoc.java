package org.nutz.doc.meta;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class ZDoc {

	public ZDoc() {
		root = new ZParagraph(this, null);
		last = root;
		authors = new LinkedList<Author>();
		verifiers = new LinkedList<Author>();
	}

	private List<Author> authors;
	private List<Author> verifiers;
	private File source;
	private ZFolder folder;

	public File getSource() {
		return source;
	}

	public ZDoc setSource(File source) {
		this.source = source;
		return this;
	}

	public ZFolder getFolder() {
		return folder;
	}

	public ZDoc setFolder(ZFolder folder) {
		this.folder = folder;
		return this;
	}

	private ZParagraph root;
	private ZParagraph last;

	public ZParagraph root() {
		return root;
	}

	public ZParagraph last() {
		return last;
	}

	public void setLast(ZParagraph last) {
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

	public Author[] getAuthors() {
		return authors.toArray(new Author[authors.size()]);
	}

	public Author[] getVerifiers() {
		return verifiers.toArray(new Author[verifiers.size()]);
	}

}
