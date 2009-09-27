package org.nutz.doc.zdoc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;
import org.nutz.doc.DocParser;
import org.nutz.doc.meta.ZBlock;
import org.nutz.doc.meta.ZDoc;
import org.nutz.doc.plain.PlainParserTest;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;

public class ZDocFileParserTest {

	static ZBlock root4file(String name) {
		String path = PlainParserTest.class.getPackage().getName().replace('.', '/') + "/" + name;
		return root(Lang.readAll(Streams.fileInr(path)));
	}

	private static ZBlock root(String s) {
		try {
			DocParser parser = new ZDocFileParser();
			ZDoc doc = parser.parse(s);
			ZBlock root = doc.root();
			return root;
		} catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
	}

	@Test
	public void test_basic_structure() {
		String s = "A";
		s = s + "\n";
		s = s + "\nB";
		s = s + "\n\tC";
		s = s + "\n\t\tD";
		s = s + "\nE";
		ZBlock root = root(s);
		assertEquals(3, root.size());
		ZBlock[] children = root.children();
		assertEquals("A", children[0].getText());
		assertEquals("B", children[1].getText());
		assertEquals("C", children[1].desc(0).ele(0).getText());
		assertEquals("D", children[1].desc(0).desc(0).ele(0).getText());
		assertEquals("E", children[2].getText());
	}

	@Test
	public void test_count_myType() {
		String s = "A";
		s += "\n\tB";
		s += "\n\t\t# C";
		s += "\n\t\t\t* D";
		s += "\n\t\t\t\t# E";
		s += "\n\t\t\t\t\t* F";
		ZBlock root = root(s);
		assertEquals(0, root.desc(0, 0, 0, 0).countMyTypeInAncestors());
		assertEquals(0, root.desc(0, 0, 0, 0, 0).countMyTypeInAncestors());
		assertEquals(1, root.desc(0, 0, 0, 0, 0, 0, 0).countMyTypeInAncestors());
		assertEquals(1, root.desc(0, 0, 0, 0, 0, 0, 0, 0, 0).countMyTypeInAncestors());
	}

	@Test
	public void test_simple_table() {
		String s = "||A||{*B}||";
		s = s + "\n||C||F[a b]E||";
		ZBlock root = root(s);

		assertTrue(root.desc(0).isTable());

		ZBlock tab = root.desc(0);
		assertTrue(tab.desc(0).isRow());
		assertTrue(tab.desc(1).isRow());

		ZBlock cellA = tab.desc(0, 0);
		assertEquals("A", cellA.getText());

		ZBlock cellB = tab.desc(0, 1);
		assertEquals("B", cellB.getText());
		assertEquals(1, cellB.eles().length);
		assertTrue(cellB.ele(0).getStyle().getFont().isBold());

		ZBlock cellC = tab.desc(1, 0);
		assertEquals("C", cellC.getText());

		ZBlock cellD = tab.desc(1, 1);
		assertEquals("FbE", cellD.getText());
		assertEquals(3, cellD.eles().length);
		assertEquals("F", cellD.ele(0).getText());
		assertEquals("b", cellD.ele(1).getText());
		assertEquals("a", cellD.ele(1).getHref().value());
		assertEquals("E", cellD.ele(2).getText());
	}
}
