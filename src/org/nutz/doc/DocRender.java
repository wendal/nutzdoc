package org.nutz.doc;

import org.nutz.doc.meta.ZDoc;

public interface DocRender<T> {

	T render(ZDoc doc);

}
