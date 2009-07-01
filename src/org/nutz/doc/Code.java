package org.nutz.doc;

public class Code extends Line {

	public static enum TYPE {
		Unknown, HTML, JAVA, JSON, Javascript, SQL, XML
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
