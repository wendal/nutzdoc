package org.nutz.doc.plain;

class Token {

	Token(char name, String s) {
		this.name = name;
		this.content = s;
	}

	private char name;
	private String content;

	public char getName() {
		return name;
	}

	public String getContent() {
		return content;
	}

}
