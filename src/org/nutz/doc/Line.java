package org.nutz.doc;

import java.util.Iterator;
import java.util.List;

import org.nutz.doc.style.Style;
import org.nutz.lang.Strings;

public class Line extends Ele implements Text {

	private List<Inline> inlines;
	private List<Line> children;
	private Line parent;
	private int deep;
	private Doc doc;

	public Doc getDoc() {
		return doc;
	}

	void setDoc(Doc document) {
		this.doc = document;
	}

	protected Line() {
		super();
		inlines = Doc.LIST(Inline.class);
		children = Doc.LIST(Line.class);
	}

	public int deep() {
		return deep;
	}

	Line setDeep(int deep) {
		this.deep = deep;
		for (Line l : children)
			l.setDeep(deep + 1);
		return this;
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

	public void addChild(Line l) {
		l.parent(this);
		l.setDoc(doc);
		l.setDeep(this.deep + 1);
		children.add(l);
	}

	public void addChild(int index, Line l) {
		l.parent(this);
		l.setDoc(doc);
		l.setDeep(this.deep + 1);
		children.add(index, l);
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

	public Iterator<Line> childIterator() {
		return children.iterator();
	}

	public Line[] children() {
		return children.toArray(new Line[children.size()]);
	}

	public String getText() {
		StringBuilder sb = new StringBuilder();
		for (Text t : inlines)
			sb.append(t.getText());
		return sb.toString();
	}

	public boolean isBlank() {
		for (Text t : inlines)
			if (!t.isBlank())
				return false;
		return true;
	}

	public void setText(String text) {
		inlines.clear();
		inlines.add(Doc.inline(text));
	}

	public Line insert(Inline ele) {
		ele.setBlock(this);
		inlines.add(0, ele);
		return this;
	}

	public Line insert(String text) {
		return insert(Doc.inline(text));
	}

	public Line append(Inline ele) {
		ele.setBlock(this);
		inlines.add(ele);
		return this;
	}

	public Line append(String text) {
		return append(Doc.inline(text));
	}

	public Line clearInlines() {
		inlines.clear();
		return this;
	}

	public Iterator<Inline> iterator() {
		return inlines.iterator();
	}

	public Inline[] inlines() {
		return inlines.toArray(new Inline[inlines.size()]);
	}

	public Inline inline(int index) {
		return inlines.get(index);
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
		sb.append(Strings.dup('\t', deep() - 1));
		sb.append(getText());
		for (Line b : children)
			sb.append("\n").append(b);
		return sb.toString();
	}

	private static class ParagraphStack {

		private List<Paragraph> ps;
		private List<Line> stack;

		ParagraphStack() {
			ps = Doc.LIST(Paragraph.class);
			stack = Doc.LIST(Line.class);
		}

		@SuppressWarnings("unchecked")
		<T extends Line> Class<T> getLastLineType() {
			if (stack.size() == 0)
				return null;
			return (Class<T>) stack.get(stack.size() - 1).getClass();
		}

		void push(Line line) {
			if (line instanceof Code || line instanceof Including || line instanceof IndexTable) {
				if (stack.size() > 0) {
					ps.add(new Paragraph(stack));
					stack = Doc.LIST(Line.class);
				}
				ps.add(new Paragraph(line));
				return;
			}
			if (line.isBlank()) {
				if (stack.size() > 0) {
					ps.add(new Paragraph(stack));
					stack = Doc.LIST(Line.class);
				}
				return;
			}
			if (stack.size() > 0 && line.getClass() != getLastLineType()) {
				ps.add(new Paragraph(stack));
				stack = Doc.LIST(Line.class);
			}
			stack.add(line);
		}

		Paragraph[] getParagraphs() {
			if (stack.size() > 0) {
				ps.add(new Paragraph(stack));
				stack = Doc.LIST(Line.class);
			}
			return ps.toArray(new Paragraph[ps.size()]);
		}

	}

	public Paragraph[] getParagraphs() {
		ParagraphStack ps = new ParagraphStack();
		for (Line l : children)
			ps.push(l);
		return ps.getParagraphs();
	}

	public boolean isHeading() {
		if (this instanceof ListItem)
			return false;
		return children.size() > 0;
	}

	public <T extends Line> boolean contains(Class<T> type) {
		if (type.isInstance(this))
			return true;
		for (Line sub : children)
			if (sub.contains(type))
				return true;
		return false;
	}

	public <T extends Line> int countAncestor(Class<T> type) {
		if (!hasParent())
			return 0;
		return parent().countAncestor(type) + (this.getClass().isAssignableFrom(type) ? 1 : 0);
	}

	public int countMyTypes() {
		return countAncestor(this.getClass());
	}
}
