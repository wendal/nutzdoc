package org.nutz.doc.meta;

import org.nutz.lang.util.IntRange;

public class ZDocs {

	public static ZBlock p(String text) {
		ZBlock p = p();
		p.append(ele(text));
		return p;
	}

	public static ZBlock p() {
		return new ZBlock();
	}

	public static ZBlock hr() {
		return block(ZType.HR);
	}

	public static ZBlock block(ZType type) {
		return new ZBlock().setType(type);
	}

	public static ZBlock code() {
		return block(ZType.CODE);
	}

	public static ZBlock ol() {
		return block(ZType.OL);
	}

	public static ZBlock ul() {
		return block(ZType.UL);
	}

	public static ZBlock index(IntRange ir) {
		return p().setIndexRange(ir);
	}

	public static ZBlock br() {
		return p("\n");
	}

	public static ZBlock table() {
		return p().setType(ZType.TABLE);
	}

	public static ZBlock row() {
		return block(ZType.ROW);
	}

	public static ZEle ele(String text) {
		return new ZEle(text);
	}

	public static ZRefer refer(String str) {
		return new ZRefer(str);
	}

	public static ZColor color(String str) {
		return new ZColor(str);
	}

	public static ZColor color() {
		return new ZColor();
	}

	public static Author author(String str) {
		return new Author(str);
	}

	public static ZStyle style() {
		return new ZStyle();
	}
}
