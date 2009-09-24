package org.nutz.doc;

import org.nutz.doc.meta.ZEle;
import org.nutz.doc.meta.ZParagraph;
import org.nutz.doc.meta.ZRefer;

public class ZDocs {

	public static ZParagraph p(String text) {
		return p().append(ele(text));
	}

	public static ZParagraph p() {
		return new ZParagraph();
	}

	public static ZEle ele(String text) {
		return new ZEle(text);
	}

	public static ZRefer refer(String refer) {
		ZRefer r = new ZRefer();
		r.setPath(refer);
		return r;
	}

}
