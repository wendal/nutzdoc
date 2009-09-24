package org.nutz.doc.text.acc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nutz.doc.meta.ZEle;
import org.nutz.doc.meta.ZParagraph;
import org.nutz.doc.text.EleAcceptor;

import static org.nutz.doc.ZDocs.*;

public class ImageAcceptor implements EleAcceptor {

	private StringBuilder sb;

	public ImageAcceptor() {
		sb = new StringBuilder();
	}

	@Override
	public boolean accept(char c) {
		if (c == '>')
			return false;
		sb.append(c);
		return true;
	}

	private static Pattern PTN = Pattern.compile("^([0-9]+)([Xx])([0-9]+)(:)(.+)$");

	@Override
	public void update(ZParagraph p) {
		ZEle ele = ele(null);
		String s = sb.toString();
		Matcher m = PTN.matcher(sb);
		if (m.find()) {
			ele.setSrc(refer(m.group(5)));
			ele.setWidth(Integer.parseInt(m.group(1)));
			ele.setHeight(Integer.parseInt(m.group(3)));
		} else {
			ele.setSrc(refer(s));
		}
		p.append(ele);
	}
}
