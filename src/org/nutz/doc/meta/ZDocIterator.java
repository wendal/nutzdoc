package org.nutz.doc.meta;

import java.util.Iterator;

import org.nutz.lang.Lang;
import org.nutz.lang.util.LinkedIntArray;

public class ZDocIterator implements Iterator<ZBlock> {

	private ZBlock root;

	/**
	 * Down, push Up: pop When empty,
	 */
	private LinkedIntArray cursor;
	private ZBlock me;

	ZDocIterator(ZBlock block) {
		cursor = new LinkedIntArray(20);
		root = block;
		if (!root.hasChildren())
			me = root;
		else
			me = root.child(0);
		cursor.push(0);
	}

	public boolean hasNext() {
		return me != root;
	}

	public ZBlock next() {
		if (me == root)
			return null;
		ZBlock re = me;
		// Down: push
		if (me.hasChildren()) {
			moveDown();
		}
		// Up: pop
		else if (is_last_child_of_my_parent()) {
			moveUp();
			moveNext();
		}
		// Same level
		else {
			moveNext();
		}
		return re;
	}

	private boolean is_last_child_of_my_parent() {
		return me.getParent().size() == (cursor.last() + 1);
	}

	private void moveDown() {
		if (me == root)
			return;
		me = me.child(0);
		cursor.push(0);
	}

	private void moveUp() {
		if (me == root)
			return;
		cursor.popLast();
		me = me.getParent();
		if (me == root)
			return;
		if (is_last_child_of_my_parent())
			moveUp();
	}

	private void moveNext() {
		if (me == root)
			return;
		int index = cursor.popLast() + 1;
		me = me.getParent().child(index);
		cursor.push(index);
	}

	public void remove() {
		throw Lang.makeThrow("No implement yet!");
	}

}
