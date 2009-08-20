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
		if(null==name())
			return "";
		String s = name().replace("<", "&lt;");
		s = s.replace(">", "&gt;");
		return s;
	}
}
