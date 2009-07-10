package org.nutz.doc.tool;

import java.io.File;

import org.nutz.lang.Files;
import org.nutz.lang.Strings;

import static java.lang.System.*;

public class File2XML {

	public static void main(String[] args) {
		String path = args[0];
		final String regex = args.length > 1 ? args[1] : null;
		File home = Files.findFile(path);
		printXml(home, regex, 0);
	}

	private static void printXml(File dir, String regex, int deep) {
		File[] fs = dir.listFiles();
		for (File f : fs)
			if (f.isFile() && f.getName().matches(regex)) {
				String nm = Files.getName(f);
				out.printf("%s<doc path=\"%s\" text=\"%s\"/>\n", Strings.dup('\t', deep), nm, Strings
						.capitalize(nm));
			}

		for (File f : fs)
			if (f.isDirectory() && !f.getName().startsWith(".")) {
				String nm = f.getName();
				out.printf("%s<doc path=\"%s\" text=\"%s\">\n", Strings.dup('\t', deep), nm, Strings
						.capitalize(nm));
				printXml(f, regex, deep + 1);
				out.printf("%s</doc>\n", Strings.dup('\t', deep));
			}
	}

}
