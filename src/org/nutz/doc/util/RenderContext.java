package org.nutz.doc.util;

import org.nutz.lang.util.LinkedArray;

public class RenderContext<T> {

	private LinkedArray<T> stack;

	public RenderContext() {
		stack = new LinkedArray<T>(20);
	}

	public int size() {
		return stack.size();
	}

	public RenderContext<T> push(T obj) {
		stack.push(obj);
		return this;
	}

	public T pop() {
		return stack.popLast();
	}

	public T last() {
		return stack.last();
	}

}
