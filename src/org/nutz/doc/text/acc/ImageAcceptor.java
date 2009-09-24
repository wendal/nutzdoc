package org.nutz.doc.text.acc;

import java.util.regex.Matcher;

import java.util.regex.Pattern;

import org.nutz.doc.EleSet;
import org.nutz.doc.meta.ZEle;

import static org.nutz.doc.meta.ZDocs.*;

public class ImageAcceptor extends PureTextAcceptor {

	private StringBuilder sb;

	public ImageAcceptor() {
		sb = new StringBuilder();
	}

	public boolean accept(char c) {
		if (c == '>')
			return false;
		sb.append(c);
		return true;
	}

	private static Pattern PTN = Pattern.compile("^([0-9]+)([Xx])([0-9]+)(:)(.+)$");

	public void update(EleSet p) {
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
