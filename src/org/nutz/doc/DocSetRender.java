package org.nutz.doc;

import java.io.IOException;

import org.nutz.doc.meta.ZDocSet;

public interface DocSetRender {

	void render(String dest, ZDocSet set) throws IOException;

}
