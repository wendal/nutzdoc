package org.nutz.doc;

import java.util.Iterator;
import java.util.List;

public class Block {

	protected List<Line> lines;

	Block(Line line) {
		this.lines = Doc.LIST(Line.class);
		this.lines.add(line);
	}

	Block(List<Line> lines) {
		this.lines = lines;
	}

	void addLine(Line line) {
		lines.add(line);
	}

	public Iterator<Line> iterator() {
		return lines.iterator();
	}

	public Line[] lines() {
		return lines.toArray(new Line[lines.size()]);
	}

	public ListItem[] items() {
		return lines.toArray(new ListItem[lines.size()]);
	}

	public int size() {
		return lines.size();
	}

	public Line line(int index) {
		return lines.get(index);
	}

	public ListItem li(int index) {
		return (ListItem) line(index);
	}

	public boolean isOrderedList() {
		return isList(OrderedListItem.class);
	}

	public boolean isUnorderedList() {
		return isList(UnorderedListItem.class);
	}

	public boolean isHeading() {
		for (Line l : lines)
			if(!l.isHeading())
				return false;
		return true;
	}
	
	public boolean isHr(){
		return (lines.size() == 1 && lines.get(0) instanceof HorizontalLine);
	}

	public boolean isIndexTable() {
		return (lines.size() == 1 && lines.get(0) instanceof IndexTable);
	}

	public boolean isCode() {
		return (lines.size() == 1 && lines.get(0) instanceof Code);
	}

	private <T extends ListItem> boolean isList(Class<T> listType) {
		boolean re = true;
		for (Line l : lines) {
			if (l.isBlank())
				continue;
			if (!listType.isInstance(l))
				return false;
			else if (!re)
				re = true;
		}
		return re;
	}
}
