package org.nutz.doc.html;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;
import org.nutz.doc.DocParser;
import org.nutz.doc.meta.ZBlock;
import org.nutz.doc.meta.ZDoc;
import org.nutz.doc.zdoc.ZDocParser;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Tag;

public class HtmlDocRenderTest {

	private static ZBlock root(String s) {
		DocParser parser = new ZDocParser();
		ZDoc doc = parser.parse(s);
		ZBlock root = doc.root();
		return root;
	}

	private static String render(String name) {
		File src = Files.findFile("org/nutz/doc/html/" + name + "/src.zdoc");
		String s = Lang.readAll(Streams.fileInr(src));
		ZDocParser parser = new ZDocParser();
		ZDoc doc = parser.parse(s);
		HtmlDocRender render = new HtmlDocRender();
		return Strings.trim(render.render(doc).toString()).replace("\r", "");

	}

	private static String expect(String name) {
		File expect = Files.findFile("org/nutz/doc/html/" + name + "/expect.html");
		return Strings.trim(Lang.readAll(Streams.fileInr(expect))).replace("\r", "");
	}

	@Test
	public void t1() {
		String actual = render("t1");
		String expect = expect("t1");
		assertEquals(expect, actual);
	}

	@Test
	public void someEles() {
		HtmlDocRender render = new HtmlDocRender();
		String expect = "<span style=\"color:#FF0000;\"><b>A</b></span>";
		String s = "{#F00;*A}";
		ZBlock root = root(s);

		Tag tag = render.renderEle(root.child(0).ele(0));
		assertEquals(expect, tag.toString());
	}

}
