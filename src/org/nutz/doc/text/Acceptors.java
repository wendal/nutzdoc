package org.nutz.doc.text;

import org.nutz.doc.text.EleAcceptor;
import org.nutz.doc.text.acc.*;
import org.nutz.lang.util.LinkedCharArray;

public class Acceptors {

	private static char[] CS = { '{', '<', '`', '[' };

	static boolean isJudgeTime(LinkedCharArray cache, char c) {
		if (c == '\n')
			return true;
		if (c == ':')
			return true;
		if (c == '|' && cache.size() > 2)
			return true;
		for (char ch : CS)
			if (ch == c)
				return true;
		return false;
	}

	static BlockAcceptor evalBlockAcceptor(String s) {
		String low = s.toLowerCase();
		if (low.startsWith("#index:"))
			return new IndexAcceptor();

		if (low.startsWith("#author:"))
			return new AuthorAcceptor();

		if (low.startsWith("#verifier:"))
			return new VerifierAcceptor();

		if (low.startsWith("||"))
			return new RowAcceptor();

		return new DefaultAcceptor();
	}

	public static EleAcceptor evalEleAcceptor(char c) {
		switch (c) {
		case '{':
			return new StyleAcceptor();
		case '<':
			return new ImageAcceptor();
		case '`':
			return new EscapingAcceptor();
		case '[':
			return new HrefAcceptor();
		}
		return null;
	}

}
