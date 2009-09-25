package org.nutz.doc.zdoc;

import static org.nutz.doc.meta.ZDocs.*;


import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.nutz.doc.meta.ZBlock;
import org.nutz.doc.meta.ZDoc;
import org.nutz.lang.util.LinkedCharArray;

class Parsing {

	private ZDoc doc;
	private BufferedReader reader;

	Parsing(BufferedReader reader) {
		this.doc = new ZDoc();
		this.reader = reader;
	}

	ZDoc getDoc() {
		return doc;
	}

	void parse() throws IOException {
		Scanning scanning = new Scanning(reader);
		scanning.scan();
		transform(doc.root(), scanning.get());
	}

	private void transform(ZBlock p, Line line) {
		if (line.withoutChild())
			return;

		Iterator<Line> it = line.it();
		LinkedList<Line> stack = new LinkedList<Line>();
		while (it.hasNext()) {
			line = it.next();
			// #title:
			if (null != line.getTitle()) {
				p.getDoc().setTitle(line.getText());
				continue;
			}
			// #author:
			if (null != line.getAuthor()) {
				p.getDoc().addAuthor(line.getAuthor());
				continue;
			}
			// #verifier:
			if (null != line.getVerifier()) {
				p.getDoc().addVerifier(line.getVerifier());
				continue;
			}
			// #index:
			if (null != line.getIndexRange()) {
				p.add(index(line.getIndexRange()));
				transform(p, line);
				continue;
			}
			// HR
			if (line.isHr()) {
				p.add(hr());
				transform(p, line);
				continue;
			}

			// Push line to stack
			if (stack.isEmpty()) {
				stack.add(line);
				continue;
			}
			// If is code, loop for the code end
			Line last = stack.getLast();
			if (last.isCodeStart()) {
				while (!line.isCodeEnd()) {
					stack.add(line);
					line = it.next();
				}
				p.add(makeBlockAndClearStack(stack));
				continue;
			}
			// If current is blank, we will create a paragrah
			if (line.isBlank()) {
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
			if (last.isEndByEscape()) {
				last.join(line);
				continue;
			}
			// If line type changed, that's mean we need make a block
			// else, just push line to stack.
			if (last.type != line.type) {
				p.add(makeBlockAndClearStack(stack));
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
				stack.push(line);
			} else {
				stack.add(line);
			}
		}
		if (!stack.isEmpty())
			p.add(makeBlockAndClearStack(stack));
	}

	static ZBlock toBlock(char[] cs) {
		return new BlockMaker(cs).make();
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
				ZBlock li = line.toBlock().setType(first.type);
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
					row.add(toBlock(j.next().toArray()));
				}
				re.add(row);
			}
		}
		// Code
		else if (first.isCodeStart()) {
			re = code();
			re.setTitle(first.getCodeType());
			Iterator<Line> it = stack.iterator();
			StringBuilder sb = new StringBuilder();
			while (it.hasNext()) {
				Line line = it.next();
				sb.append(line.toString());
			}
			re.setText(sb.toString());
		}
		// Normal
		else {
			StringBuilder sb = new StringBuilder();
			Iterator<Line> it = stack.iterator();
			while (it.hasNext())
				sb.append(it.next().getText());
			re = toBlock(sb.toString().toCharArray());
			if (stack.getLast().hasChild())
				transform(re, stack.getLast());
		}
		// Clear the stack
		stack.clear();
		return re;
	}

	private static final char[] CELL_BORDER = { '|', '|' };

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
			} else if (stack.endsWith(CELL_BORDER)) {
				list.add(stack.popLast(2));
			}
		}
		return list;
	}
}
