package org.nutz.doc.text.acc;

import java.util.ArrayList;

import org.nutz.doc.EleSet;
import org.nutz.doc.meta.ZEle;

class EleCache implements EleSet {

	private ArrayList<ZEle> list;

	public EleCache() {
		list = new ArrayList<ZEle>();
	}

	public EleSet append(ZEle ele) {
		list.add(ele);
		return this;
	}

	int size() {
		return list.size();
	}

	boolean isEndBy2Br() {
		if (list.size() >= 0)
			if (list.get(list.size() - 1).isBr() && list.get(list.size() - 2).isBr())
				return true;
		return false;
	}

	ZEle ele(int index) {
		return list.get(index);
	}

	public ZEle[] eles() {
		return list.toArray(new ZEle[list.size()]);
	}

}
