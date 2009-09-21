package org.nutz.doc.meta;

import java.util.LinkedList;
import java.util.List;

public class ZParagraph {

	private ZDoc doc;
	private List<ZEle> eles;
	private List<ZParagraph> children;
	private ZParagraph parent;

	public ZParagraph(ZDoc doc, ZParagraph parent) {
		this.parent = parent;
		this.doc = doc;
		this.eles = new LinkedList<ZEle>();
		this.children = new LinkedList<ZParagraph>();
	}

	public ZParagraph append(ZEle ele) {
		eles.add(ele.setParagraph(this));
		return this;
	}

	public ZEle[] eles() {
		return eles.toArray(new ZEle[eles.size()]);
	}

	public ZParagraph add(ZParagraph p) {
		p.parent = this;
		children.add(p);
		doc.setLast(p);
		return this;
	}

	public ZParagraph[] children() {
		return children.toArray(new ZParagraph[children.size()]);
	}

	public boolean hasChildren() {
		return children.size() > 0;
	}

	public ZParagraph getParent() {
		return parent;
	}

	public ZDoc getDoc() {
		return doc;
	}

	public static enum ZTYPE {
		OL, UL, CODE, TABLE, ROW
	}

	private ZTYPE type;

	public ZTYPE getType() {
		return type;
	}

	public ZParagraph setType(ZTYPE type) {
		this.type = type;
		return this;
	}

	public boolean isOL() {
		return ZTYPE.OL == type;
	}

	public boolean isUL() {
		return ZTYPE.UL == type;
	}

	public boolean isCode() {
		return ZTYPE.CODE == type;
	}

	public boolean isTable() {
		return ZTYPE.TABLE == type;
	}

	public boolean isRow() {
		return ZTYPE.ROW == type;
	}

	public boolean isNormal() {
		return null == type;
	}

	public boolean isRoot() {
		return null == parent;
	}

}
