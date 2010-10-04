package org.nutz.doc.zdoc;

import static org.nutz.doc.meta.ZD.refer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nutz.doc.meta.ZBlock;
import org.nutz.doc.meta.ZD;
import org.nutz.doc.meta.ZFont;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Context;
import org.nutz.lang.util.LinkedCharArray;

public class BlockMaker {

	private char[] cs;
	private EleHolder ep;
	private int i;
	private ZBlock block;
	private LinkedCharArray endles;
	private Context context;

	BlockMaker(Context context, char[] cs) {
		this.context = context == null ? new Context() : context;
		this.cs = cs;
		this.ep = new EleHolder();
		this.block = ZD.p();
		this.endles = new LinkedCharArray();
	}

	ZBlock make() {
		reading();
		updateBlock();
		return block;
	}

	private void reading() {
		for (; i < cs.length; i++) {
			char c = cs[i];
			switch (c) {
			case '$':
				forVariable();
				break;
			case '{':
				forStyle();
				break;
			case '`':
				forEscaping();
				break;
			case '<':
				forImage();
				break;
			case '[':
				forLink();
				break;
			default:
				if (endles.last() == c) {
					endles.popLast();
					return;
				}
				ep.sb.append(c);
			}
		}
	}

	private void forVariable() {
		// Current one must be '$' so, read next one, to see it is '{' or not
		char c = cs[++i];
		if (c != '{') {
			ep.sb.append('$');
			if (endles.last() == c)
				endles.popLast();
			else
				ep.sb.append(c);
			return;
		}

		// Then read until found the '}'
		StringBuilder sb = new StringBuilder();
		for (++i; i < cs.length; i++) {
			c = cs[i];
			if ('}' == c)
				break;
			sb.append(c);
		}
		// Get the value from context
		String val = context.getString(sb.toString());
		ep.sb.append(val);
	}

	private void forStyle() {
		updateBlock();
		for (++i; i < cs.length; i++) {
			char c = cs[i];
			// Color
			if (c == '#') {
				forColor();
			}
			// Bold
			else if (c == '*') {
				ep.ele.style().font().addStyle(ZFont.BOLD);
			}
			// Italic
			else if (c == '/') {
				ep.ele.style().font().addStyle(ZFont.ITALIC);
			}
			// Underline
			else if (c == '_') {
				ep.ele.style().font().addStyle(ZFont.UNDERLINE);
			}
			// Strike
			else if (c == '~') {
				ep.ele.style().font().addStyle(ZFont.STRIKE);
			}
			// Sup
			else if (c == '^') {
				ep.ele.style().font().addStyle(ZFont.SUP);
			}
			// Sub
			else if (c == ',') {
				ep.ele.style().font().addStyle(ZFont.SUB);
			}
			// Then try to read content, end by '}'
			else {
				endles.push('}');
				reading();
				break;
			}
		}
		updateBlock();
	}

	private void forColor() {
		StringBuilder sb = new StringBuilder();
		for (++i; i < cs.length; i++)
			if (cs[i] == ';')
				break;
			else
				sb.append(cs[i]);
		ep.ele.style().font().setColor(sb.toString());
	}

	private void forEscaping() {
		for (++i; i < cs.length; i++) {
			char c = cs[i];
			if (c == '`') {
				if (ep.sb.length() == 0) {
					ep.sb.append(c);
				}
				return;
			}
			ep.sb.append(c);
		}
	}

	private static Pattern PtnImg = Pattern.compile("^([0-9]*)([xX])([0-9]*)([:])(.*)$");

	private void forImage() {
		updateBlock();
		StringBuilder sb = new StringBuilder();
		for (++i; i < cs.length; i++) {
			if (cs[i] == '>')
				break;
			sb.append(cs[i]);
		}
		Matcher m = PtnImg.matcher(sb);
		if (m.find()) {
			ep.ele.setSrc(refer(m.group(5)));
			ep.ele.setWidth(Strings.isBlank(m.group(1)) ? 0 : Integer.parseInt(m.group(1)));
			ep.ele.setHeight(Strings.isBlank(m.group(3)) ? 0 : Integer.parseInt(m.group(3)));
		} else {
			ep.ele.setSrc(refer(sb.toString()));
		}
		updateBlock();
	}

	private void forLink() {
		updateBlock();
		StringBuilder sb = new StringBuilder();
		for (++i; i < cs.length; i++) {
			char c = cs[i];
			if (c == ' ' || c == '\t') {
				i++;
				break;
			} else if (c == ']')
				break;
			sb.append(c);
		}
		ep.ele.setHref(refer(sb.toString()));
		if (cs[i] != ']') {
			endles.push(']');
			reading();
		}
		updateBlock();
	}

	private void updateBlock() {
		if (endles.isEmpty())
			ep.resetAndSaveTo(block);
	}

}
