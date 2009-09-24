package org.nutz.doc;

import org.nutz.doc.meta.ZEle;

public interface EleSet {

	public abstract EleSet append(ZEle ele);

	public abstract ZEle[] eles();

}