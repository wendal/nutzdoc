package org.nutz.doc;

import java.util.Iterator;
import java.util.List;

import org.nutz.doc.style.Style;
import org.nutz.lang.Strings;

public class Line extends Ele implements Text {

	private List<Inline> inlines;
	protected List<Line> children;
	private Line parent;
	private int depth;
	private Doc doc;

	public Doc getDoc() {
		if (hasParent())
			return parent.getDoc();
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

	public int depth() {
		return depth;
	}

	Line setDepth(int depth) {
		this.depth = depth;
		for (Line l : children)
			l.setDepth(depth + 1);
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
		l.setDepth(this.depth + 1);
		children.add(l);
	}

	public void addChild(int index, Line l) {
		l.parent(this);
		l.setDoc(doc);
		l.setDepth(this.depth + 1);
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

	public Line child(int... path) {
		Line l = this;
		for (int index : path)
			l = l.child(index);
		return l;
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

	public <T extends Line> boolean is(Class<T> type) {
		return this.getClass() == type;
	}

	public Line insert(Inline ele) {
		ele.setLine(this);
		inlines.add(0, ele);
		return this;
	}

	public Line insert(String text) {
		return insert(Doc.inline(text));
	}

	public Line append(Inline ele) {
		ele.setLine(this);
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
		sb.append(Strings.dup('\t', depth()));
		sb.append(getText());
		for (Line b : children)
			sb.append("\n").append(b);
		return sb.toString();
	}

	private static class BlockStack {

		private List<Block> ps;
		private List<Line> stack;

		BlockStack() {
			ps = Doc.LIST(Block.class);
			stack = Doc.LIST(Line.class);
		}

		void push(Line line) {
			if (line instanceof ZRow) {
				if (stack.size() == 0) {
					stack.add(line);
				} else {
					Line first = stack.get(0);
					if (!(first instanceof ZRow))
						popStack();
					stack.add(line);
				}
			} else if (line instanceof Code || line instanceof IndexTable) {
				popStack();
				ps.add(new Block(line));
			} else if (line.isBlank()) {
				popStack();
				if (line instanceof HorizontalLine)
					ps.add(new Block(line));
			} else {
				if (stack.size() > 0) {
					Line last = stack.get(stack.size() - 1);
					if (line.getClass() != last.getClass())
						popStack();
					else if (last.isHeading())
						popStack();
				}
				stack.add(line);
			}
		}

		private void popStack() {
			if (stack.size() > 0) {
				if (stack.get(0) instanceof ZRow)
					ps.add(new Shell(stack));
				else
					ps.add(new Block(stack));
				stack = Doc.LIST(Line.class);
			}
		}

		Block[] getBlock() {
			popStack();
			return ps.toArray(new Block[ps.size()]);
		}

	}

	public Block[] getBlocks() {
		BlockStack ps = new BlockStack();
		for (Line l : children)
			ps.push(l);
		return ps.getBlock();
	}

	public boolean isHeading() {
		if (!is(Line.class))
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

	public <T extends Line> int countTypes(Class<T> type) {
		if (!hasParent())
			return 0;
		int me = this.getClass() == (type) ? 1 : 0;
		int p = parent().countTypes(type);
		return p + me;
	}

	public int countMyTypeInAncestors() {
		if (!hasParent())
			return 0;
		return parent().countTypes(this.getClass());
	}

	public List<Media> getMedias() {
		List<Media> medias = Doc.LIST(Media.class);
		for (Inline il : inlines)
			if (il instanceof Media)
				medias.add((Media) il);
		for (Line l : children)
			medias.addAll(l.getMedias());
		return medias;
	}

	public void removeIndexTable() {
		List<Line> list = children;
		children = Doc.LIST(Line.class);
		for (Line l : list) {
			if (l instanceof IndexTable)
				continue;
			l.removeIndexTable();
			addChild(l);
		}
	}
}
