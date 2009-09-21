package org.nutz.doc;

import org.nutz.doc.meta.ZEle;
import org.nutz.doc.meta.ZParagraph;

public class ZDocs {

	public static ZParagraph p(String text) {
		return new ZParagraph().append(ele(text));
	}

	public static ZEle ele(String text) {
		return new ZEle(text);
	}

}
