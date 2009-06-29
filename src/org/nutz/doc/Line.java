package org.nutz.doc;

import java.util.Iterator;
import java.util.List;

import org.nutz.doc.style.Style;
import org.nutz.lang.Strings;

public class Line extends Ele implements Text {

	private List<Inline> eles;
	private List<Line> children;
	private Line parent;
	private int deep;

	Line() {
		super();
		eles = Doc.list(Inline.class);
		children = Doc.list(Line.class);
	}

	public int deep() {
		return deep;
	}

	public Line parent() {
		return parent;
	}

	public boolean hasParent() {
		return null != parent;
	}

	private void parent(Line parent) {
		this.parent = parent;
	}

	public void addChild(Line block) {
		block.parent(this);
		block.deep = this.deep + 1;
		children.add(block);
	}

	public void addChild(int index, Line block) {
		block.parent(this);
		block.deep = this.deep + 1;
		children.add(index, block);
	}

	public void clearChildren() {
		children.clear();
	}

	public Line removeChild(int index) {
		return children.remove(index);
	}

	public int size() {
		return children.size();
	}

	public Line child(int index) {
		return children.get(index);
	}

	public Line lastChild() {
		if (0 == children.size())
			return null;
		return children.get(children.size() - 1);
	}

	public Line firstChild() {
		if (0 == children.size())
			return null;
		return children.get(0);
	}

	public Iterator<Line> children() {
		return children.iterator();
	}

	public String getText() {
		StringBuilder sb = new StringBuilder();
		for (Text t : eles)
			sb.append(t.getText());
		return sb.toString();
	}

	public boolean isBlank() {
		for (Text t : eles)
			if (!t.isBlank())
				return false;
		return true;
	}

	public void setText(String text) {
		eles.clear();
		eles.add(Doc.inline(text));
	}

	public Line append(Inline ele) {
		ele.setBlock(this);
		eles.add(ele);
		return this;
	}

	public Line append(String text) {
		return append(Doc.inline(text));
	}

	public Line clearInlines() {
		eles.clear();
		return this;
	}

	public Iterator<Inline> iterator() {
		return eles.iterator();
	}

	public Inline[] eles() {
		return eles.toArray(new Inline[eles.size()]);
	}

	public Inline ele(int index) {
		return eles.get(index);
	}

	public Style getRealStyle() {
		if (hasStyle())
			if (hasParent())
				return getStyle().merge(parent.getRealStyle());
			else
				return getStyle();
		else if (hasParent())
			return parent.getRealStyle();
		return null;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (Text t : eles)
			sb.append(Strings.dup('\t', deep()-1)).append(t).append("\n");
		for (Line b : children)
			sb.append(b);
		return sb.toString();
	}
}
