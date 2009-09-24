package org.nutz.doc.text.acc;

import org.nutz.doc.meta.ZDoc;

public class TitleAcceptor extends LineAcceptor {

	public void update(ZDoc doc) {
		doc.setTitle(getText());
	}

}
