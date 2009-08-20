package org.nutz.doc.plain;

class TokenPair {

	private char begin;
	private char end;
	private boolean escape;

	TokenPair(char begin, char end) {
		this(begin, end, false);
	}

	TokenPair(char begin, char end, boolean escape) {
		this.begin = begin;
		this.end = end;
		this.escape = escape;
	}

	public char getBegin() {
		return begin;
	}

	public char getEnd() {
		return end;
	}

	public boolean isEscape() {
		return escape;
	}

}
