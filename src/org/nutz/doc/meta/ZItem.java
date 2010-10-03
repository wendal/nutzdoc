package org.nutz.doc.meta;

public abstract class ZItem {

	public abstract String getTitle();

	public abstract ZItem setTitle(String title);

	public abstract ZItem addAuthor(String author);

	public abstract ZItem addVerifier(String verifier);

	public abstract Author[] authors();

	public abstract boolean hasAuthor();

	public abstract boolean hasVerifier();

	public abstract Author[] verifiers();

}
