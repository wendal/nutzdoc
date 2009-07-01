package org.nutz.doc.plain;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.doc.Including;
import org.nutz.doc.Inline;
import org.nutz.doc.Line;
import org.nutz.doc.DocParser;
import org.nutz.doc.Doc;
import org.nutz.doc.Media;
import org.nutz.lang.Lang;

public class PlainParserTest {

	private static Line root(String s) {
		DocParser parser = new PlainParser();
		Doc doc = parser.parse(Lang.inr(s));
		Line root = doc.root();
		return root;
	}

	@Test
	public void test_basic_structure() {
		String s = "A\nB\n\tC\n\t\tD\nE";
		Line root = root(s);
		assertEquals(3, root.size());
		assertEquals("A", root.child(0).getText());
		assertEquals("B", root.child(1).getText());
		assertEquals("C", root.child(1).child(0).inline(0).getText());
		assertEquals("D", root.child(1).child(0).child(0).inline(0).getText());
		assertEquals("E", root.child(2).getText());
	}

	@Test
	public void test_inline_styles() {
		String s = "A{~_*^,E}C";
		Line root = root(s);
		Line line = root.child(0);
		assertEquals("AEC", line.getText());
		Inline[] eles = line.inlines();
		assertEquals(3, eles.length);
		assertEquals("A", eles[0].getText());
		assertFalse(eles[0].hasStyle());
		assertTrue(eles[1].hasStyle());
		assertTrue(eles[1].getStyle().getFont().isBold());
		assertTrue(eles[1].getStyle().getFont().isStrike());
		assertTrue(eles[1].getStyle().getFont().isItalic());
		assertTrue(eles[1].getStyle().getFont().isSub());
		assertFalse(eles[1].getStyle().getFont().isSup());
		assertFalse(eles[2].hasStyle());
	}

	@Test
	public void test_inline_anchor() {
		String s = "nutz: [http://nutz.googlecode.com]";
		Line root = root(s);

		Inline e = root.child(0).inline(1);
		assertEquals("http://nutz.googlecode.com", e.getText());
		assertEquals("http://nutz.googlecode.com", e.getHref().toString());
		assertTrue(e.isAnchor());
	}

	@Test
	public void test_inline_anchor_text() {
		String s = "Google: [http://www.google.com Google]";
		Line root = root(s);

		Inline e = root.child(0).inline(1);
		assertEquals("Google", e.getText());
		assertEquals("http://www.google.com", e.getHref().toString());
		assertTrue(e.isAnchor());
	}

	@Test
	public void test_inline_media_png() {
		String s = "Image: [org/nutz/doc/plain/nutz.png]";
		Line root = root(s);

		Inline e = root.child(0).inline(1);
		assertTrue(e instanceof Media);
		assertNull(e.getHref());
		assertFalse(e.isAnchor());
	}

	@Test
	public void test_inline_media_anchor() {
		String s = "Nutz: [http://nutz.googlecode.com org/nutz/doc/plain/nutz.png]";
		Line root = root(s);

		Media media = (Media) root.child(0).inline(1);
		assertTrue(media.src().isLocal());
		assertTrue(media.src().getFile().exists());
		assertEquals("nutz.png", media.src().getFile().getName());
		assertEquals("http://nutz.googlecode.com", media.getHref().toString());
	}

	@Test
	public void test_style_inline_anchor() {
		String s = "Nutz: {*~^[http://nutz.googlecode.com Code]} Y";
		Inline e = root(s).child(0).inline(1);

		assertTrue(e.getStyle().getFont().isBold());
		assertTrue(e.getStyle().getFont().isStrike());
		assertTrue(e.getStyle().getFont().isSup());
		assertFalse(e.getStyle().getFont().isSub());
		assertFalse(e.getStyle().getFont().isItalic());
		assertEquals("Code", e.getText());
		assertEquals("http://nutz.googlecode.com", e.getHref().toString());

	}

	@Test
	public void test_multiple_line_with_inline_anchor() {
		String s = "A";
		s += "\n\tB";
		s += "\n\t\tA[b.gif]{*~^[http://nutz.googlecode.com Code]}";
		Line line = root(s).child(0).child(0).child(0);

		assertEquals("A", line.inline(0).toString());
		assertTrue(line.inline(1) instanceof Media);
		assertEquals("b.gif", ((Media) line.inline(1)).getSrc());

		Inline e = line.inline(2);
		assertTrue(e.getStyle().getFont().isBold());
		assertTrue(e.getStyle().getFont().isStrike());
		assertTrue(e.getStyle().getFont().isSup());
		assertFalse(e.getStyle().getFont().isSub());
		assertFalse(e.getStyle().getFont().isItalic());
		assertEquals("Code", e.getText());
		assertEquals("http://nutz.googlecode.com", e.getHref().toString());
	}

	@Test
	public void test_simple_include() {
		String s = "doc1";
		s += "\n\t@include: org/nutz/doc/plain/doc.txt";
		s += "\ndoc2";
		s += "\n\t@>include: org/nutz/doc/plain/doc.txt";
		Line root = root(s);
		assertEquals("doc1", root.child(0).getText());
		assertEquals("doc2", root.child(1).getText());

		Line line = root.child(0).child(0);
		assertEquals("A: ", line.inline(0).toString());
		assertEquals("http://www.google.com", line.inline(1).getText());
		assertEquals("http://www.google.com", line.inline(1).getHref().toString());

		line = line.child(0);
		assertEquals("B: ", line.inline(0).toString());
		assertEquals("Google", line.inline(1).getText());
		assertTrue(line.inline(1).getStyle().getFont().isBold());
		assertEquals("http://www.google.com", line.inline(1).getHref().toString());

		line = line.child(0);
		assertEquals("C: ", line.inline(0).toString());
		Media media = (Media) line.inline(1);
		assertEquals("nutz.png", media.src().getFile().getName());
		assertEquals("http://nutz.googlecode.com", media.getHref().toString());

		Including inc = (Including) root.child(1).child(0);
		assertTrue(inc.getRefer().isLocal());
		assertEquals("doc.txt", inc.getRefer().getFile().getName());
	}
	
	@Test
	public void test_simple_include_same_level(){
		String s = "doc1";
		s += "\n@include: org/nutz/doc/plain/doc.txt";
		s += "\ndoc2";
		
		Line root = root(s);
		assertEquals("doc1", root.child(0).getText());
		assertEquals("doc2", root.child(2).getText());
		
		Line line = root.child(1);
		assertEquals("A: ", line.inline(0).toString());
		assertEquals("http://www.google.com", line.inline(1).getText());
		assertEquals("http://www.google.com", line.inline(1).getHref().toString());

		line = line.child(0);
		assertEquals("B: ", line.inline(0).toString());
		assertEquals("Google", line.inline(1).getText());
		assertTrue(line.inline(1).getStyle().getFont().isBold());
		assertEquals("http://www.google.com", line.inline(1).getHref().toString());

		line = line.child(0);
		assertEquals("C: ", line.inline(0).toString());
		Media media = (Media) line.inline(1);
		assertEquals("nutz.png", media.src().getFile().getName());
		assertEquals("http://nutz.googlecode.com", media.getHref().toString());
	}
}
