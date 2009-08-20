package org.nutz.doc.plain;

import java.util.List;

import org.nutz.doc.Doc;
import org.nutz.lang.util.LinkedCharArray;

class TokenWalker {

	private char[] cs;
	private int cursor;
	private List<TokenPair> pairs;

	TokenWalker add(char b, char e) {
		pairs.add(new TokenPair(b, e));
		return this;
	}

	TokenWalker add(char b, char e, boolean escape) {
		pairs.add(new TokenPair(b, e, escape));
		return this;
	}

	TokenWalker(char[] cs) {
		this.cs = cs;
		this.pairs = Doc.LIST(TokenPair.class);
	}

	TokenWalker(String str) {
		this(str.toCharArray());
	}

	Token next() {
		if (cursor >= cs.length)
			return null;
		TokenPair tp = findPair(cs[cursor]);
		int begin = cursor;
		cursor++;
		// Not a token
		if (null == tp) {
			for (; cursor < cs.length; cursor++) {
				char c = cs[cursor];
				if (null != findPair(c))
					break;
			}
			return new Token((char) 0, String.valueOf(cs, begin, cursor - begin));
		}
		// For escape
		if (tp.isEscape()) {
			char c = cs[cursor];
			while (c != tp.getEnd() && cursor < cs.length)
				c = cs[++cursor];
			if (cursor == begin + 1) {
				cursor++;
				return new Token(tp.getBegin(), String.valueOf(c));
			}
			cursor++;
			return new Token(tp.getBegin(), String.valueOf(cs, ++begin, cursor - begin - 1));
		}
		// For normal token
		LinkedCharArray stack = new LinkedCharArray();
		stack.push(tp.getEnd());
		StringBuilder sb = new StringBuilder();
		while (stack.size() > 0) {
			if (cursor >= cs.length)
				break;
			char c = cs[cursor++];
			if (stack.last() == c) {
				stack.popLast();
				if (stack.size() > 0)
					sb.append(c);
				continue;
			}
			TokenPair tp2 = findPair(c);
			if (null != tp2)
				stack.push(tp2.getEnd());
			sb.append(c);
		}
		return new Token(tp.getBegin(), sb.toString());
	}

	private TokenPair findPair(char c) {
		for (TokenPair p : pairs)
			if (p.getBegin() == c)
				return p;
		return null;
	}

}
