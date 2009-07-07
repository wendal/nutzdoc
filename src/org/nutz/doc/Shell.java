package org.nutz.doc;

import java.util.List;

public class Shell extends Block {

	Shell(Line line) {
		super(line);
	}

	Shell(List<Line> lines) {
		super(lines);
	}

	public ZRow[] rows() {
		return lines.toArray(new ZRow[size()]);
	}

}
