package org.nutz.doc.text.acc;

import org.nutz.doc.EleSet;
import org.nutz.doc.text.EleAcceptor;

public class PureTextAcceptor implements EleAcceptor {

	public boolean accept(char c) {
		return false;
	}

	public void update(EleSet p) {}

}
