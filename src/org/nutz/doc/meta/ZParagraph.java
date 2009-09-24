package org.nutz.doc.meta;

import java.util.LinkedList;
import java.util.List;

public class ZParagraph {

	private ZDoc doc;
	private List<ZEle> eles;
	private List<ZParagraph> children;
	private ZParagraph parent;

	public ZParagraph() {
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
		p.setParent(this);
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

	public ZParagraph setParent(ZParagraph parent) {
		this.parent = parent;
		this.setDoc(parent.getDoc());
		return this;
	}

	public ZParagraph getParent() {
		return parent;
	}

	public ZDoc getDoc() {
		return doc;
	}

	public ZParagraph setDoc(ZDoc doc) {
		this.doc = doc;
		for (ZParagraph chd : children)
			chd.setDoc(doc);
		return this;
	}

	public String getText() {
		StringBuilder sb = new StringBuilder();
		for (ZEle ele : eles)
			sb.append(ele.getText());
		return sb.toString();
	}

	/**
	 * Zero base, doc.root.depth == 0 ;
	 * 
	 * @return
	 */
	public int depth() {
		if (null == parent)
			return 0;
		return parent.depth() + 1;
	}

	public static enum ZTYPE {
		OL, UL, CODE, TABLE, ROW, HR
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

	public boolean isHr() {
		return ZTYPE.HR == type;
	}

	public boolean isRoot() {
		return null == parent;
	}

	public boolean isCanBeParent() {
		return isOL() || isUL() || isNormal();
	}

}
