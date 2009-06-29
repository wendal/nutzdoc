package org.nutz.doc;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

public class Href {

	private String link;

	Href(String link) {
		if (Strings.isBlank(link))
			throw Lang.makeThrow("Href can not be null!!!");
		this.link = link.replace('\\', '/');
	}

	public boolean isInner() {
		return link.startsWith("#");
	}
	
	public boolean isHttp(){
		return link.matches("^(http://|https://)");
	}
	
	public boolean isAbsolute(){
		return link.matches("^(/|[a-zA-Z]:/)");
	}

	@Override
	public String toString() {
		return link;
	}

}
