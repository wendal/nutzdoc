package org.nutz.doc;

import java.io.IOException;

import org.nutz.doc.meta.ZDoc;

public interface DocRender {

	CharSequence render(ZDoc doc) throws IOException;

}
