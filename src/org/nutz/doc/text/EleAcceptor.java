package org.nutz.doc.text;

import org.nutz.doc.EleSet;

public interface EleAcceptor {

	boolean accept(char c);

	void update(EleSet p);
	
}
