package org.nutz.doc.text.acc;

import org.nutz.doc.meta.ZDoc;
import org.nutz.doc.meta.ZDocs;

public class VerifierAcceptor extends LineAcceptor {

	@Override
	public void update(ZDoc doc) {
		doc.addVerifier(ZDocs.author(getText()));
	}

}
