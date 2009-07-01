package org.nutz.doc.html;

import java.util.List;

import org.nutz.doc.Doc;
import org.nutz.lang.meta.Pair;

public class Tag {

	private String name;
	private Tag parent;
	private List<Tag> children;
	private List<Pair> attributes;

	public Tag(String name) {
		this.name = name;
		children = Doc.list(Tag.class);
		attributes = Doc.list(Pair.class);
	}

}
