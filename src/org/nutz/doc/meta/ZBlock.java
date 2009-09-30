package org.nutz.doc.meta;

import java.util.ArrayList;

import java.util.Iterator;
import java.util.List;

import org.nutz.lang.Strings;
import org.nutz.lang.util.IntRange;
import org.nutz.lang.util.LinkedIntArray;
import org.nutz.lang.util.Node;
import org.nutz.lang.util.Nodes;

public class ZBlock {

	private ZType type;
	private String title;
	private ZDoc doc;
	List<ZEle> eles;
	List<ZBlock> children;
	private ZBlock parent;
	private IntRange indexRange;

	ZBlock() {
		this.eles = new ArrayList<ZEle>();
		this.children = new ArrayList<ZBlock>();
	}

	public ZBlock append(ZEle ele) {
		eles.add(ele.setBlock(this));
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
		return this;
	}

	public ZBlock[] children() {
		return children.toArray(new ZBlock[children.size()]);
	}

	public ZBlock desc(int... indexes) {
		ZBlock re = this;
		for (int i : indexes)
			re = re.child(i);
		return re;
	}

	public ZBlock child(int index) {
		return children.get(index);
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

	public String getString() {
		if (isHr())
			return Strings.dup('-', 10);
		if (null != getIndexRange()) {
			return String.format("#index:%s", getIndexRange().toString());
		}
		StringBuilder sb = new StringBuilder();
		for (ZEle ele : eles)
			sb.append(ele.toString());
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

	public boolean isOLI() {
		return ZType.OLI == type;
	}

	public boolean isUL() {
		return ZType.UL == type;
	}

	public boolean isULI() {
		return ZType.ULI == type;
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
		return isNormal() && children.size() > 0;
	}

	public boolean isBlank() {
		return Strings.isBlank(getText());
	}

	/**
	 * Alias of childCount()
	 */
	public int size() {
		return children.size();
	}

	public int childCount() {
		return children.size();
	}

	public int eleCount() {
		return eles.size();
	}

	public String toString() {
		return toString(0);
	}

	String toString(int depth) {
		StringBuilder sb = new StringBuilder();
		sb.append(Strings.dup('\t', depth)).append(symbol()).append(getString()).append('\n');
		sb.append(getChildrenString(depth));
		return sb.toString();
	}

	String getChildrenString() {
		return getChildrenString(null != parent ? 0 : -1);
	}

	String getChildrenString(int depth) {
		StringBuilder sb = new StringBuilder();
		Iterator<ZBlock> it = children.iterator();
		while (it.hasNext())
			sb.append(it.next().toString(depth + 1));
		return sb.toString();
	}

	String symbol() {
		if (ZType.OL == type || ZType.UL == type) {
			return String.format("%s - %d items", type.name(), children.size());
		}
		if (ZType.OLI == type)
			return " # ";
		if (ZType.ULI == type)
			return " * ";
		return "";
	}

	public Iterator<ZBlock> iterator() {
		return new ZDocIterator(this);
	}

	public Node<ZIndex> buildIndex(IntRange range) {
		LinkedIntArray nums = new LinkedIntArray(20);
		Node<ZIndex> root = Nodes.create(ZDocs.index(null, null, doc.getTitle()));
		nums.push(-1);
		for (ZBlock chd : children) {
			if (chd.isHeading()) {
				nums.push(nums.popLast() + 1);
				_buildIndex(nums, root, range, chd);
			}
		}
		return root;
	}

	private static void _buildIndex(LinkedIntArray nums, Node<ZIndex> node, IntRange range,
			ZBlock me) {
		int lvl = me.depth() - 1;
		int depth = nums.size();
		if (range.inon(lvl)) {
			Node<ZIndex> newNode = Nodes.create(ZDocs.index("#" + me.getId(), nums.toArray(), me
					.getText()));
			node.add(newNode);
			node = newNode;
		}
		if (!range.lt(lvl)) {
			nums.push(-1);
			for (ZBlock chd : me.children) {
				if (chd.isHeading()) {
					nums.push(nums.popLast() + 1);
					_buildIndex(nums, node, range, chd);
				}
			}
		}
		if (nums.size() > depth)
			nums.popLast(nums.size() - depth);
	}

	public List<ZEle> getImages() {
		List<ZEle> list = new ArrayList<ZEle>();
		Iterator<ZBlock> it = iterator();
		while (it.hasNext()) {
			ZBlock p = it.next();
			for (ZEle ele : p.eles) {
				if (ele.isImage())
					list.add(ele);
			}
		}
		return list;
	}

	public List<ZEle> getLinks() {
		List<ZEle> list = new ArrayList<ZEle>();
		Iterator<ZBlock> it = iterator();
		while (it.hasNext()) {
			ZBlock p = it.next();
			for (ZEle ele : p.eles) {
				if (ele.hasHref())
					list.add(ele);
			}
		}
		return list;
	}

	public String getId() {
		return "N" + doc.getId(this);
	}
}
