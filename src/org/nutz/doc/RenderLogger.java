package org.nutz.doc;

import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class RenderLogger {
	
	Log log = Logs.getLog(getClass());

	private void log(String fmt, Object... args) {
		log.infof(fmt, args);
	}

	public void log1(String fmt, Object... args) {
		log(" " + fmt, args);
	}

	public void log2(String fmt, Object... args) {
		log(Strings.dup(' ', 4) + fmt, args);
	}

	public void log3(String fmt, Object... args) {
		log(Strings.dup(' ', 8) + fmt, args);
	}

	public void log4(String fmt, Object... args) {
		log(Strings.dup(' ', 12) + fmt, args);
	}

}
