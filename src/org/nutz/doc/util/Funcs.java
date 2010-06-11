package org.nutz.doc.util;

public abstract class Funcs {

	public static String evalAnchorName(String text) {
		StringBuilder sb = new StringBuilder();
		char[] cs = text.toCharArray();
		for (int i = 0; i < cs.length; i++) {
			switch (cs[i]) {
			case ' ':
			case '\t':
				if (sb.length() > 0 && sb.charAt(sb.length() - 1) != '_') {
					sb.append('_');
				}
				break;
			case '\r':
			case '\n':
			case '"':
			case '.':
			case '\'':
				break;
			default:
				sb.append(cs[i]);
			}
		}
		return sb.toString();
	}

}
