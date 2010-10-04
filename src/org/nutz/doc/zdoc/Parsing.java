package org.nutz.doc.zdoc;

import static org.nutz.doc.meta.ZD.*;

import java.io.BufferedReader;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.nutz.doc.meta.ZBlock;
import org.nutz.doc.meta.ZDoc;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Context;
import org.nutz.lang.util.LinkedCharArray;

class Parsing {

	private ZDoc doc;
	private BufferedReader reader;
	private Context context;

	Parsing(BufferedReader reader) {
		this.reader = reader;
		this.context = new Context().set("now", Calendar.getInstance());
	}

	ZDoc parse(int tabpar) {
		ScanResult sr = new Scanning(tabpar).scan(reader);
		doc = sr.doc();
		if (!Strings.isBlank(doc.getTitle()))
			context.set("title", doc.getTitle());
		transform(doc.root(), sr.root());
		return doc;
	}

	private void transform(ZBlock p, Line lineOfP) {
		if (lineOfP.withoutChild())
			return;

		Iterator<Line> it = lineOfP.children().iterator();
		LinkedList<Line> stack = new LinkedList<Line>();
		while (it.hasNext()) {
			Line line = it.next();
			// #index:
			if (null != line.getIndexRange()) {
				p.add(range(line.getIndexRange()));
				transform(p, line);
				continue;
			}
			// HR
			if (line.isHr()) {
				if (!stack.isEmpty())
					p.add(makeBlockAndClearStack(stack));
				p.add(hr());
				continue;
			}
			// Code
			if (line.isCodeStart()) {
				if (!stack.isEmpty())
					p.add(makeBlockAndClearStack(stack));
				p.add(code(line.getCodeType(), line.getText()));
				continue;
			}

			// Push line to stack
			if (stack.isEmpty()) {
				stack.add(line);
				continue;
			}

			/*
			 * Then let's do check more complex cases...
			 */
			Line last = stack.getLast();

			// If current is blank, we will create a paragrah
			if (line.isBlank()) {
				// If last of stack is List item, just ignore the line
				if (last.isOLI() || last.isULI())
					continue;
				// Stack is only one blank line, drop all
				// This code also ensure: stack can not exist
				// more than one blank line.
				if (stack.size() == 1 && stack.getFirst().isBlank()) {
					stack.clear();
					continue;
				}
				// current line is blank, that's mean all items in stack
				// should be a paragraph
				//
				// In fact, when makeBlock() this function will be called again
				// so it is a chain:
				// transform() -> makeBlockAndClearStack() -> transform()
				p.add(makeBlockAndClearStack(stack));
				continue;
			}
			// Let me considering the last element of the stack
			/*
			 * if (last.isEndByEscaping()) { // Append all children for (Line
			 * chd : line.children()) last.add(chd); // Join to last
			 * last.join(line.getText()); continue; }
			 */
			// If line type changed, that's mean we need make a block
			// else, just push line to stack.
			if (last.type != line.type) {
				p.add(makeBlockAndClearStack(stack));
				stack.addFirst(line);
			}
			/**
			 * For the case:
			 * 
			 * <pre>
			 * Heading1
			 * 		Text
			 * Heading2
			 * </pre>
			 * 
			 * we need make Heading1, when we encounter Heading2
			 */
			else if (last.hasChild() && last.isNormal()) {
				p.add(makeBlockAndClearStack(stack));
				stack.addFirst(line);
			} else {
				stack.add(line);
			}
		}
		if (!stack.isEmpty())
			p.add(makeBlockAndClearStack(stack));
	}

	private ZBlock toBlock(char[] cs) {
		return new BlockMaker(context, cs).make();
	}

	private ZBlock makeBlockAndClearStack(LinkedList<Line> stack) {
		ZBlock re;
		Line first = stack.getFirst();
		// UL | OL
		if (first.isOLI() || first.isULI()) {
			re = first.isOLI() ? ol() : ul();
			Iterator<Line> it = stack.iterator();
			while (it.hasNext()) {
				Line line = it.next();
				ZBlock li = toBlock(line.getCharArray()).setType(first.type);
				re.add(li);
				transform(li, line);
			}
		}
		// Table
		else if (first.isRow()) {
			re = table();
			Iterator<Line> it = stack.iterator();
			while (it.hasNext()) {
				ZBlock row = row();
				List<LinkedCharArray> list = findCells(it.next().getText().toCharArray());
				Iterator<LinkedCharArray> j = list.iterator();
				while (j.hasNext()) {
					char[] cs = j.next().toArray();
					row.add(toBlock(cs));
				}
				re.add(row);
			}
		}
		// Normal
		else {
			StringBuilder sb = new StringBuilder();
			Iterator<Line> it = stack.iterator();
			while (it.hasNext()) {
				Line line = it.next();
				if (!line.isBlank())
					sb.append(line.getText());
			}
			re = toBlock(sb.toString().toCharArray());
			if (stack.getLast().hasChild())
				transform(re, stack.getLast());
		}
		// Clear the stack
		stack.clear();
		return re;
	}

	private static final char[] CELL_BORDER = {'|', '|'};

	private List<LinkedCharArray> findCells(char[] cs) {
		List<LinkedCharArray> list = new LinkedList<LinkedCharArray>();
		LinkedCharArray stack = new LinkedCharArray(256);
		boolean escape = false;
		for (int i = 2; i < cs.length; i++) {
			char c = cs[i];
			if (escape) {
				stack.push(c);
				if (c == '`')
					escape = false;
			} else if (c == '`') {
				stack.push(c);
				escape = true;
			} else {
				stack.push(c);
			}
			if (stack.endsWith(CELL_BORDER)) {
				list.add(stack.popLast(2));
				stack = new LinkedCharArray(256);
			}
		}
		return list;
	}
}
