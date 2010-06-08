package org.nutz.doc.meta;

import java.util.LinkedList;
import java.util.List;

public class ZItem {

	protected List<Author> authors;
	protected List<Author> verifiers;

	public ZItem() {
		authors = new LinkedList<Author>();
		verifiers = new LinkedList<Author>();
	}

	private String title;

	public String getTitle() {
		return title;
	}

	public ZItem setTitle(String title) {
		this.title = title;
		return this;
	}

	public ZItem addAuthor(Author author) {
		this.authors.add(author);
		return this;
	}

	public ZItem addVerifier(Author verifier) {
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

}
