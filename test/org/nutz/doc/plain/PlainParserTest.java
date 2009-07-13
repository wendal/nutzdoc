package org.nutz.doc.plain;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;
import org.nutz.doc.ZRow;
import org.nutz.doc.Code;
import org.nutz.doc.IndexTable;
import org.nutz.doc.Inline;
import org.nutz.doc.Line;
import org.nutz.doc.DocParser;
import org.nutz.doc.Doc;
import org.nutz.doc.ListItem;
import org.nutz.doc.Media;
import org.nutz.doc.OrderedListItem;
import org.nutz.doc.Block;
import org.nutz.doc.Shell;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;

public class PlainParserTest {

	private static Line root4file(String name) {
		String path = PlainParserTest.class.getPackage().getName().replace('.', '/') + "/" + name;
		return root(Lang.readAll(Streams.fileInr(path)));
	}

	private static Line root(String s) {
		DocParser parser = new PlainParser();
		Doc doc = parser.parse(Lang.ins(s));
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
		String s = "Image: <org/nutz/doc/plain/nutz.png>";
		Line root = root(s);

		Inline e = root.child(0).inline(1);
		assertTrue(e instanceof Media);
		assertNull(e.getHref());
		assertFalse(e.isAnchor());
	}

	@Test
	public void test_inline_local_media_anchor() {
		String s = "Nutz: [http://nutz.googlecode.com <org/nutz/doc/plain/nutz.png>]";
		Line root = root(s);

		Media media = (Media) root.child(0).inline(1);
		assertTrue(media.src().isLocal());
		assertTrue(media.src().getFile().exists());
		assertEquals("nutz.png", media.src().getFile().getName());
		assertEquals("http://nutz.googlecode.com", media.getHref().toString());
	}

	@Test
	public void test_inline_remote_media_anchor() {
		String s = "Nutz: [http://nutz.googlecode.com <http://www.nutz.com/nutz.png>]";
		Line root = root(s);

		Media media = (Media) root.child(0).inline(1);
		assertFalse(media.src().isLocal());
		assertTrue(media.src().isHttp());
		assertEquals("http://www.nutz.com/nutz.png", media.src().toString());
		assertEquals("http://nutz.googlecode.com", media.getHref().toString());
	}

	@Test
	public void test_inline_media_anchor_as_child() {
		String s = "* Nutz:\n\t[http://nutz.googlecode.com <http://www.nutz.com/nutz.png>]";
		Line root = root(s);
		assertTrue(root.child(0).child(0).inline(0) instanceof Media);
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
		s += "\n\t\tA<b.gif>{*~^[http://nutz.googlecode.com Code]}";
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
		s += "\n\ttxt";
		Line root = root(s);
		assertEquals(1, root.size());
		assertEquals("doc1", root.child(0).getText());

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

		assertEquals("txt", root.child(0).child(1).getText());
	}

	@Test
	public void test_simple_include_same_level() {
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

	@Test
	public void test_parse_code() throws IOException {
		Line root = root4file("code.txt");
		Line line1 = root.child(0);
		Code code1 = (Code) root.child(1);
		Line line2 = root.child(2);
		Code code2 = (Code) line2.child(0);
		Line line3 = root.child(3);
		assertEquals("This is the example java code:", line1.toString());
		assertEquals(Code.TYPE.java, code1.getType());
		assertEquals("public class Abc{\n\tprivate int num;\n\t\t// tt\n}", code1.getText());
		assertEquals("Child:", line2.getText());
		assertEquals("DROP TABLE t_abc;\n\t\t/*t*/", code2.getText());
		assertEquals("-The end-", line3.toString());
	}

	@Test
	public void test_parse_code_with_child() {
		Line root = root("{{{\nB\n}}}\n\tX");
		assertEquals(2, root.size());
		assertEquals("X", root.child(1).getText());
	}

	@Test
	public void test_index_table() {
		Line root = root4file("indexTable_1.txt");
		IndexTable it = (IndexTable) root.child(0);
		assertEquals("ABC", root.child(1).getText());
		assertEquals("F", root.child(1).child(0).getText());
		Line index = root.getDoc().getIndex(Doc.indexTable("0,1"));
		assertEquals("ABC", index.child(0).getText());
		assertEquals("L1", index.child(1).getText());
		assertEquals("L1.1", index.child(1).child(0).getText());
		assertEquals("L2", index.child(2).getText());
		assertEquals("L2.1", index.child(2).child(0).getText());
		index = root.getDoc().getIndex(it);
		assertEquals("L1.1", index.child(0).getText());
		assertEquals("L1.1.1", index.child(0).child(0).getText());
		assertEquals("L2.1", index.child(1).getText());
		assertEquals("L2.1.1", index.child(1).child(0).getText());
	}

	@Test
	public void test_eval_blocks() {
		Line root = root("A\nB\nC\n\nD\n\tD1\nE\n\nF");
		Block[] bs = root.getBlocks();
		assertEquals(4, bs.length);

		assertEquals(3, bs[0].size());
		assertEquals("A", bs[0].line(0).getText());
		assertEquals("B", bs[0].line(1).getText());
		assertEquals("C", bs[0].line(2).getText());

		assertEquals(1, bs[1].size());
		assertEquals("D", bs[1].line(0).getText());
		assertEquals("D1", bs[1].line(0).child(0).getText());

		assertEquals(1, bs[2].size());
		assertEquals("E", bs[2].line(0).getText());

		assertEquals(1, bs[3].size());
		assertEquals("F", bs[3].line(0).getText());
	}

	@Test
	public void test_tab_at_the_line_head() {
		Line root = root("A\n\t\tB");
		assertEquals("B", root.child(0).child(0).getText());
	}

	@Test
	public void test_count_myType() {
		String s = "A";
		s += "\n\tB";
		s += "\n\t\t# C";
		s += "\n\t\t\t* D";
		s += "\n\t\t\t\t# E";
		s += "\n\t\t\t\t\t* F";
		Line root = root(s);
		assertEquals(0, root.child(0, 0, 0).countMyTypeInAncestors());
		assertEquals(0, root.child(0, 0, 0, 0).countMyTypeInAncestors());
		assertEquals(1, root.child(0, 0, 0, 0, 0).countMyTypeInAncestors());
		assertEquals(1, root.child(0, 0, 0, 0, 0, 0).countMyTypeInAncestors());
	}

	@Test
	public void test_list_item() {
		Line root = root4file("list.txt");
		assertEquals(4, root.size());
		Block[] ps = root.child(0).getBlocks();
		Block[] ps2;
		assertEquals(1, ps.length);
		assertTrue(ps[0].isUnorderedList());

		ListItem li = ps[0].li(0);
		assertEquals("a", li.getText());
		ps2 = li.getBlocks();
		assertEquals(1, ps2.length);
		assertTrue(ps2[0].isOrderedList());
		assertEquals("a1", ps2[0].line(0).getText());
		assertEquals("a2", ps2[0].line(1).getText());

		li = ps[0].li(1);
		assertEquals("b", li.getText());
		ps2 = li.getBlocks();
		assertEquals(1, ps2.length);
		assertTrue(ps2[0].isUnorderedList());
		assertEquals("b1", ps2[0].line(0).getText());
		assertEquals("b2", ps2[0].line(1).getText());

		li = ps[0].li(2);
		assertEquals("c", li.getText());
		ps2 = li.getBlocks();
		assertEquals(2, ps2.length);
		assertTrue(ps2[0].isUnorderedList());
		assertTrue(ps2[1].isOrderedList());
		assertEquals("c1", ps2[0].line(0).getText());
		assertEquals("c2", ps2[1].line(0).getText());

		li = ps[0].li(3);
		assertEquals("d", li.getText());
		ps2 = li.getBlocks();
		assertEquals(2, ps2.length);
		assertTrue(ps2[1].isOrderedList());
		assertEquals("d1", ps2[0].line(0).getText());
		assertEquals("d2", ps2[1].line(0).getText());

		Line line2 = root.child(1);
		assertEquals(0, line2.size());

		assertTrue(root.child(2).isBlank());

		assertEquals("-End-", root.child(3).getText());
	}

	@Test
	public void test_list_as_child_of_line() {
		Line root = root("A\n\t* LI\nB");
		Block[] bs = root.getBlocks();
		assertEquals(2, bs.length);
		assertTrue(bs[0].isHeading());
		assertEquals("B", bs[1].line(0).getText());
		Block[] bss = bs[0].getBlocks();
		assertEquals(1, bss.length);
		assertTrue(bss[0].isUnorderedList());
	}

	@Test
	public void test_nesting_list() {
		Line root = root("# A\n\t# B");
		assertEquals("A", root.child(0).getText());
		assertTrue((root.child(0) instanceof OrderedListItem));

		assertEquals("B", root.child(0).child(0).getText());
		assertTrue((root.child(0).child(0) instanceof OrderedListItem));

		Block[] ps = root.child(0).getBlocks();
		assertEquals(1, ps.length);
		assertTrue(ps[0].isOrderedList());
		assertEquals("B", ps[0].li(0).getText());
	}

	@Test
	public void test_escaping() {
		Line root = root("A`[`B`]`C");
		assertEquals("A[B]C", root.child(0).getText());
	}

	@Test
	public void test_link_with_whitespace() {
		Line root = root("[http://abc.com A B C]");
		assertEquals("http://abc.com", root.child(0).inline(0).getHref().toString());
		assertEquals("A B C", root.child(0).getText());
	}

	@Test
	public void test_hr_1() {
		Line root = root("A\n=====\nB");
		Block[] ps = root.getBlocks();
		assertEquals("A", ps[0].line(0).getText());
		assertTrue(ps[1].isHr());
		assertEquals("B", ps[2].line(0).getText());
	}

	@Test
	public void test_heading_with_including() {
		Line root = root("A\n\t#index:3\n\tB");
		assertEquals(1, root.size());
		Block[] ps = root.getBlocks();
		assertEquals("A", ps[0].line(0).getText());
		ps = ps[0].line(0).getBlocks();
		assertTrue(ps[0].isIndexTable());
		assertEquals("B", ps[1].line(0).getText());
	}

	@Test
	public void test_one_row_shell() {
		Line root = root("||A11||A12||");
		ZRow row = (ZRow) root.child(0);
		assertEquals(2, row.size());
		assertEquals("A11", row.child(0).getText());
		assertEquals("A12", row.child(1).getText());
	}

	@Test
	public void test_basic_shell() {
		Line root = root("A\n\t||C11||C12||\n\t||C21||C22||");
		assertEquals("A", root.child(0).getText());
		Shell shell = (Shell) root.child(0).getBlocks()[0];
		assertEquals(2, shell.size());
		ZRow[] rows = shell.rows();
		assertEquals(2, rows.length);
		assertEquals(2, rows[0].size());
		assertEquals(2, rows[1].size());
		assertEquals("C11", rows[0].child(0).getText());
		assertEquals("C22", rows[1].child(1).getText());
	}

	@Test
	public void test_child_of_blank_line() {
		Line root = root("A\n\n\tB\n\nC");
		assertEquals("A", root.child(0).getText());
		assertEquals("B", root.child(0).child(0).getText());
		Block[] bs = root.getBlocks();
		assertEquals(2, bs.length);
		assertEquals("A", bs[0].line(0).getText());
		assertEquals("C", bs[1].line(0).getText());
	}

	@Test
	public void test_code_with_indent() {
		Line root = root("A\n\t{{{\n\tX\n\n\tY\n\t}}}");
		assertEquals(1, root.size());
		assertEquals("A", root.child(0).getText());
		Code code = (Code) root.child(0, 0);
		assertEquals("X\n\nY", code.getText());
	}

	@Test
	public void test_get_block_with_blank() {
		Line root = root("A\n\tA1\n\n\tA2");
		Block[] bs = root.child(0).getBlocks();
		assertEquals(2, bs.length);
	}

	@Test
	public void test_nested_inlines() {
		Line root = root("A[abc.htm {_B B}]C");
		Inline[] inlines = root.child(0).inlines();
		assertEquals("A", inlines[0].getText());
		assertEquals("B B", inlines[1].getText());
		assertEquals("abc.htm", inlines[1].getHref().toString());
		assertTrue(inlines[1].getStyle().getFont().isItalic());
		assertEquals("C", inlines[2].getText());
	}
}
