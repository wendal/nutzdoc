package org.nutz.doc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.nutz.doc.html.HtmlDocRender;
import org.nutz.doc.plain.PlainParser;
import org.nutz.lang.Files;
import org.nutz.lang.Streams;

import static java.lang.System.*;

public class DOCT {

	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			error();
		}
		File src = Files.findFile(args[0]);
		if (null == src) {
			err.println("Fail to find source file: " + args[0]);
			error();
		}
		File dest = new File(args[1]);
		if (!dest.exists()) {
			out.println("Create file : " + dest.getAbsolutePath());
			Files.createNewFile(dest);
		}
		DocParser parser = new PlainParser();
		InputStream ins = Streams.fileIn(src);
		Doc doc = parser.parse(ins);
		ins.close();
		DocRender render = new HtmlDocRender();
		OutputStream ops = Streams.fileOut(dest);
		render.render(ops, doc);
		ops.close();
		out.println("Done!");
	}

	private static void error() {
		err.println("Usage:");
		err.println("zdoc [source file] [destination file]");
		exit(0);
	}

}
