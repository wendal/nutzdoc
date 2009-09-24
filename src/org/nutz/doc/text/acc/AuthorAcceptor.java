package org.nutz.doc.text.acc;

import org.nutz.doc.meta.ZDoc;
import org.nutz.doc.meta.ZDocs;

public class AuthorAcceptor extends LineAcceptor {

	public void update(ZDoc doc) {
		doc.addAuthor(ZDocs.author(getText()));
	}

}
