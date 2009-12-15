package org.nutz.doc.zdoc;

import java.io.BufferedReader;
import java.io.IOException;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

class Scanning {

	Line scan(BufferedReader reader) {
		try {
			String str;
			Line root = Line.make(null);
			Line last = root;
			while (null != (str = reader.readLine())) {
				int depth = countTab(str);
				str = Strings.trim(str);
				Line line = Line.make(str);
				// Read escaping
				if (line.endByEscape()) {
					StringBuilder sb = new StringBuilder();
					sb.append(line.symbol());
					sb.append(line.getText());
					Line nextLine = line;
					while (nextLine.endByEscape()) {
						String next = Strings.trim(reader.readLine());
						nextLine = Line.make(next);
						sb.append(' ').append(nextLine.symbol()).append(nextLine.getText());
					}
					line = Line.make(sb.toString());
				}
				/*
				 * When HR or Blank
				 */
				if (line.isHr() || line.isBlank()) {
					if (last == root) {
						last.add(line);
					} else {
						last.getParent().add(line);
					}
				} else {
					while (depth < last.depth())
						last = last.getParent();
					if (null != last.getParent())
						if (last.isHr())
							last = last.getParent();
					while (last.isBlank() && last != root) {
						if (last.getPrev() == null)
							last = last.getParent();
						else
							last = last.getPrev();
					}
					last.add(line);
				}
				last = line;
			}
			return root;
		} catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
	}

	private int countTab(String l) {
		int i = 0;
		for (; i < l.length(); i++)
			if (l.charAt(i) != '\t')
				break;
		return i;
	}

}
