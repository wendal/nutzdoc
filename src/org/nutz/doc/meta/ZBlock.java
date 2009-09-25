package org.nutz.doc.meta;

import java.util.ArrayList;
import java.util.List;

import org.nutz.doc.EleSet;
import org.nutz.lang.Strings;
import org.nutz.lang.util.IntRange;

public class ZBlock implements EleSet {

	private ZType type;
	private String title;
	private ZDoc doc;
	private List<ZEle> eles;
	private List<ZBlock> children;
	private ZBlock parent;
	private IntRange indexRange;

	ZBlock() {
		this.eles = new ArrayList<ZEle>();
		this.children = new ArrayList<ZBlock>();
	}

	public EleSet append(ZEle ele) {
		eles.add(ele.setParagraph(this));
		return this;
	}

	public ZEle[] eles() {
		return eles.toArray(new ZEle[eles.size()]);
	}

	public ZEle ele(int index) {
		return eles.get(index);
	}

	public ZBlock add(ZBlock p) {
		p.setParent(this);
		children.add(p);
		doc.setLast(p);
		return this;
	}

	public ZBlock[] children() {
		return children.toArray(new ZBlock[children.size()]);
	}

	public ZBlock child(int... indexes) {
		ZBlock re = this;
		for (int i : indexes)
			re = re.children.get(i);
		return re;
	}

	public int countMyTypeInAncestors() {
		List<ZBlock> list = ancestors();
		int re = 0;
		for (ZBlock p : list)
			if (p.type == type)
				re++;
		return re;
	}

	public List<ZBlock> ancestors() {
		List<ZBlock> list = new ArrayList<ZBlock>();
		ZBlock me = this.parent;
		do {
			list.add(me);
			me = me.parent;
		} while (me != null);
		return list;
	}

	public boolean hasChildren() {
		return children.size() > 0;
	}

	public ZBlock setParent(ZBlock parent) {
		this.parent = parent;
		this.setDoc(parent.getDoc());
		return this;
	}

	public ZBlock getParent() {
		return parent;
	}

	public ZDoc getDoc() {
		if (null != doc)
			return doc;
		if (null != parent) {
			doc = parent.getDoc();
			return doc;
		}
		return null;
	}

	public ZBlock setDoc(ZDoc doc) {
		this.doc = doc;
		for (ZBlock chd : children)
			chd.setDoc(doc);
		return this;
	}

	public String getText() {
		StringBuilder sb = new StringBuilder();
		for (ZEle ele : eles)
			sb.append(ele.getText());
		return sb.toString();
	}

	public ZBlock setText(String text) {
		eles.clear();
		append(ZDocs.ele(text));
		return this;
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

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public ZType getType() {
		return type;
	}

	public ZBlock setType(ZType type) {
		this.type = type;
		return this;
	}

	public boolean isOL() {
		return ZType.OL == type;
	}

	public boolean isUL() {
		return ZType.UL == type;
	}

	public boolean isLI() {
		return ZType.OLI == type || ZType.ULI == type;
	}

	public boolean isCode() {
		return ZType.CODE == type;
	}

	public boolean isTable() {
		return ZType.TABLE == type;
	}

	public boolean isRow() {
		return ZType.ROW == type;
	}

	public boolean isNormal() {
		return null == type;
	}

	public boolean isHr() {
		return ZType.HR == type;
	}

	public boolean isRoot() {
		return null == parent;
	}

	public boolean hasIndexRange() {
		return null != indexRange;
	}

	public IntRange getIndexRange() {
		return indexRange;
	}

	public ZBlock setIndexRange(IntRange indexRange) {
		this.indexRange = indexRange;
		return this;
	}

	public boolean isHeading() {
		return isNormal() || children.size() > 0;
	}

	public boolean isBlank() {
		return Strings.isBlank(getText());
	}

	public int size() {
		return children.size();
	}

}
