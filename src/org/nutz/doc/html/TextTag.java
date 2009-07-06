package org.nutz.doc.html;

public class TextTag extends Tag {

	TextTag(String name) {
		super(name);
	}

	public String getContent() {
		return name();
	}

	@Override
	public String toString() {
		String s = name().replaceAll("[<]", "&lt;");
		s = s.replaceAll("[>]", "&gt;");
		return s;
	}

}
