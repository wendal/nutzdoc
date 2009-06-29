package org.nutz.doc.plain;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.doc.Inline;
import org.nutz.doc.Line;
import org.nutz.doc.DocParser;
import org.nutz.doc.Document;
import org.nutz.lang.Lang;

public class PlainParserTest {

	private static Line root(String s) {
		DocParser parser = new PlainParser();
		Document doc = parser.parse(Lang.inr(s));
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
		assertEquals("C", root.child(1).child(0).ele(0).getText());
		assertEquals("D", root.child(1).child(0).child(0).ele(0).getText());
		assertEquals("E", root.child(2).getText());
	}

	@Test
	public void test_inline_styles() {
		String s = "A{-_*^,E}C";
		Line root = root(s);
		Line line = root.child(0);
		assertEquals("AEC",line.getText());
		Inline[] eles = line.eles();
		assertEquals(3,eles.length);
		assertEquals("A",eles[0].getText());
		assertFalse(eles[0].hasStyle());
		assertTrue(eles[1].hasStyle());
		assertTrue(eles[1].getStyle().getFont().isBold());
		assertTrue(eles[1].getStyle().getFont().isStrike());
		assertTrue(eles[1].getStyle().getFont().isItalic());
		assertFalse(eles[1].getStyle().getFont().isSub());
		assertTrue(eles[1].getStyle().getFont().isSup());
		assertFalse(eles[2].hasStyle());
	}
}

