package org.nutz.doc.plain;

import java.util.List;

import org.nutz.doc.Doc;

class TokenWalker {

	private char[] cs;
	private int i;
	private List<TokenPair> pairs;

	TokenWalker add(char b, char e) {
		pairs.add(new TokenPair(b, e));
		return this;
	}

	TokenWalker(char[] cs) {
		this.cs = cs;
		this.pairs = Doc.LIST(TokenPair.class);
	}

	Token next() {
		if (i >= cs.length)
			return null;
		TokenPair tp = findPair(cs[i]);
		int begin = i;
		i++;
		if (null == tp) {
			for (; i < cs.length; i++) {
				if (null != findPair(cs[i]))
					break;
			}
			return new Token((char) 0, String.valueOf(cs, begin, i - begin));
		}
		for (; i < cs.length; i++) {
			if (tp.getEnd() == cs[i])
				return new Token(tp.getBegin(), String.valueOf(cs, begin + 1, ++i - begin - 2));
		}
		return null;
	}

	private TokenPair findPair(char c) {
		for (TokenPair p : pairs)
			if (p.getBegin() == c)
				return p;
		return null;
	}

}
