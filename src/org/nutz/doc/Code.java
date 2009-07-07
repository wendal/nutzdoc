package org.nutz.doc;

public class Code extends FinalLine {

	public static enum TYPE {
		Unknown, html, java, json, javascript, sql, xml
	}

	private TYPE type;

	public TYPE getType() {
		return type;
	}

	public void setType(TYPE type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return String.format("{{{<%s>\n%s\n}}}", type, getText());
	}

}
