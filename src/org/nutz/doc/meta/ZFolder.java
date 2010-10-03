package org.nutz.doc.meta;

import java.util.List;

public class ZFolder extends ZItem {

	private String title;
	
	private List<Author> authors;
	
	private List<Author> verifiers;

	public ZFolder(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public ZFolder setTitle(String title) {
		this.title = title;
		return this;
	}
	
	
	public ZItem addAuthor(String author) {
		this.authors.add(ZD.author(author));
		return this;
	}

	public ZItem addVerifier(String verifier) {
		this.verifiers.add(ZD.author(verifier));
		return this;
	}

	public Author[] authors() {
		return authors.toArray(new Author[authors.size()]);
	}

	public boolean hasAuthor() {
		return !authors.isEmpty();
	}

	public boolean hasVerifier() {
		return !verifiers.isEmpty();
	}

	public Author[] verifiers() {
		return verifiers.toArray(new Author[verifiers.size()]);
	}

}
