package org.nutz.doc.text;

abstract class CharAcceptor {

	abstract void init(String s);

	abstract boolean accept(char c);

	abstract Object getResult();

}
