package org.nutz.doc;

import java.io.OutputStream;

public interface DocRender {

	void render(OutputStream ops, Doc doc);

}
