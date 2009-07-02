package org.nutz.doc;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import org.nutz.doc.html.HtmlDocRender;
import org.nutz.doc.plain.PlainParser;
import org.nutz.lang.Files;
import org.nutz.lang.Streams;

public class TranslateDoc {

	public static void main(String[] args) throws IOException {
		DocParser parser = new PlainParser();
		Reader reader = Streams.fileInr("org/nutz/doc/plain/trans.txt");
		Doc doc = parser.parse(reader);
		reader.close();
		DocRender render = new HtmlDocRender();
		File f = new File("e:/tmp/trans.html");
		if(!f.exists())
			Files.createNewFile(f);
		Writer writer = Streams.fileOutw(f);
		render.render(writer, doc);
		writer.close();
		System.out.println("Done!");
	}

}
