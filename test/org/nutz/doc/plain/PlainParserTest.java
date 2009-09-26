package org.nutz.doc.plain;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.junit.Test;
import org.nutz.doc.DocParser;
import org.nutz.doc.meta.*;
import org.nutz.doc.zdoc.ZDocFileParser;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.util.IntRange;

public class PlainParserTest {

	private static ZBlock root4file(String name) {
		String path = PlainParserTest.class.getPackage().getName().replace('.', '/') + "/" + name;
		return root(Lang.readAll(Streams.fileInr(path)));
	}

	private static ZBlock root(String s) {
		try {
			DocParser parser = new ZDocFileParser();
			ZDoc doc = parser.parse(TFile(s));
			ZBlock root = doc.root();
			return root;
		} catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
	}

	private static File TFile(String s) {
		File f = Files.findFile("org/nutz/doc/plain/code.txt");
		f = new File(f.getParent() + "/tmps.txt");
		if (!f.exists())
			try {
				Files.createNewFile(f);
			} catch (IOException e) {
				throw Lang.wrapThrow(e);
			}
		Lang.writeAll(Streams.fileOutw(f), s);
		return f;
	}

	@Test
	public void test_escape_in_color() {
		ZBlock root = root("{*{A}}");
		ZEle ele = root.children()[0].eles()[0];
		assertTrue(ele.getStyle().getFont().isBold());
		assertEquals("{A}", ele.getText());
	}

	@Test
	public void test_parse_media() {
		ZEle media = root("<a.gif>").children()[0].eles()[0];
		assertEquals(0, media.getHeight());
		assertEquals(0, media.getWidth());
		assertEquals("a.gif", media.getSrc().getPath());

		media = root("<10x7:a.gif>").children()[0].eles()[0];
		assertEquals(10, media.getWidth());
		assertEquals(7, media.getHeight());
		assertEquals("a.gif", media.getSrc().getPath());

		media = root("<4x4:http://www.zzh.com/a.gif>").children()[0].eles()[0];
		assertEquals(4, media.getHeight());
		assertEquals(4, media.getWidth());
		assertEquals("http://www.zzh.com/a.gif", media.getSrc().getPath());
		assertTrue(media.getSrc().isHttp());
	}

	@Test
	public void test_basic_structure() {
		String s = "A\nB\n\tC\n\t\tD\nE";
		ZBlock root = root(s);
		assertEquals(3, root.size());
		ZBlock[] children = root.children();
		assertEquals("A", children[0].getText());
		assertEquals("B", children[1].getText());
		assertEquals("C", children[1].child(0).ele(0).getText());
		assertEquals("D", children[1].child(0).child(0).ele(0).getText());
		assertEquals("E", children[2].getText());
	}

	@Test
	public void test_inZParagraph_styles() {
		String s = "A{~_*^,E}C";
		ZBlock root = root(s);
		ZBlock ZParagraph = root.child(0);
		assertEquals("AEC", ZParagraph.getText());
		ZEle[] eles = ZParagraph.eles();
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
	public void test_inZParagraph_anchor() {
		String s = "nutz: [http://nutz.googlecode.com]";
		ZBlock root = root(s);

		ZEle e = root.child(0).ele(1);
		assertEquals("http://nutz.googlecode.com", e.getText());
		assertEquals("http://nutz.googlecode.com", e.getHref().toString());
		assertTrue(e.hasHref());
	}

	@Test
	public void test_inZParagraph_anchor_text() {
		String s = "Google: [http://www.google.com Google]";
		ZBlock root = root(s);

		ZEle e = root.child(0).ele(1);
		assertEquals("Google", e.getText());
		assertEquals("http://www.google.com", e.getHref().toString());
		assertTrue(e.hasHref());
	}

	@Test
	public void test_inZParagraph_media_png() {
		String s = "Image: <org/nutz/doc/plain/nutz.png>";
		ZBlock root = root(s);

		ZEle e = root.child(0).ele(1);
		assertTrue(e.isImage());
		assertNull(e.getHref());
		assertFalse(e.hasHref());
	}

	@Test
	public void test_inZParagraph_local_media_anchor() {
		String s = "Nutz: [http://nutz.googlecode.com <org/nutz/doc/plain/nutz.png>]";
		ZBlock root = root(s);

		ZEle media = root.child(0).ele(1);
		assertTrue(media.getSrc().isLocal(media));
		assertTrue(media.getSrc().getFile(media).exists());
		assertEquals("nutz.png", media.getSrc().getFile(media).getName());
		assertEquals("http://nutz.googlecode.com", media.getHref().toString());
	}

	@Test
	public void test_inZParagraph_remote_media_anchor() {
		String s = "Nutz: [http://nutz.googlecode.com <http://www.nutz.com/nutz.png>]";
		ZBlock root = root(s);

		ZEle media = root.child(0).ele(1);
		assertFalse(media.getSrc().isLocal(media));
		assertTrue(media.getSrc().isHttp());
		assertEquals("http://www.nutz.com/nutz.png", media.getSrc().toString());
		assertEquals("http://nutz.googlecode.com", media.getHref().toString());
	}

	@Test
	public void test_inZParagraph_media_anchor_as_child() {
		String s = "* Nutz:\n\t[http://nutz.googlecode.com <http://www.nutz.com/nutz.png>]";
		ZBlock root = root(s);
		assertTrue(root.child(0).child(0).ele(0).isImage());
	}

	@Test
	public void test_style_inZParagraph_anchor() {
		String s = "Nutz: {*~^[http://nutz.googlecode.com Code]} Y";
		ZEle e = root(s).child(0).ele(1);

		assertTrue(e.getStyle().getFont().isBold());
		assertTrue(e.getStyle().getFont().isStrike());
		assertTrue(e.getStyle().getFont().isSup());
		assertFalse(e.getStyle().getFont().isSub());
		assertFalse(e.getStyle().getFont().isItalic());
		assertEquals("Code", e.getText());
		assertEquals("http://nutz.googlecode.com", e.getHref().toString());

	}

	@Test
	public void test_multiple_ZParagraph_with_inZParagraph_anchor() {
		String s = "A";
		s += "\n\tB";
		s += "\n\t\tA<b.gif>{*~^[http://nutz.googlecode.com Code]}";
		ZBlock p = root(s).child(0).child(0).child(0);

		assertEquals("A", p.ele(0).toString());
		assertTrue(p.ele(1).isImage());
		assertEquals("b.gif", (p.ele(1)).getSrc());

		ZEle e = p.ele(2);
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
		ZBlock root = root(s);
		assertEquals(1, root.size());
		assertEquals("doc1", root.child(0).getText());

		ZBlock ZParagraph = root.child(0).child(0);
		assertEquals("A: ", ZParagraph.ele(0).toString());
		assertEquals("http://www.google.com", ZParagraph.ele(1).getText());
		assertEquals("http://www.google.com", ZParagraph.ele(1).getHref().toString());

		ZParagraph = ZParagraph.child(0);
		assertEquals("B: ", ZParagraph.ele(0).toString());
		assertEquals("Google", ZParagraph.ele(1).getText());
		assertTrue(ZParagraph.ele(1).getStyle().getFont().isBold());
		assertEquals("http://www.google.com", ZParagraph.ele(1).getHref().toString());

		ZParagraph = ZParagraph.child(0);
		assertEquals("C: ", ZParagraph.ele(0).toString());
		ZEle media = ZParagraph.ele(1);
		assertEquals("nutz.png", media.getSrc().getFile(media).getName());
		assertEquals("http://nutz.googlecode.com", media.getHref().toString());

		assertEquals("txt", root.child(0).child(1).getText());
	}

	@Test
	public void test_simple_include_same_level() {
		String s = "doc1";
		s += "\n@include: org/nutz/doc/plain/doc.txt";
		s += "\ndoc2";

		ZBlock root = root(s);
		assertEquals("doc1", root.child(0).getText());
		assertEquals("doc2", root.child(2).getText());

		ZBlock ZParagraph = root.child(1);
		assertEquals("A: ", ZParagraph.ele(0).toString());
		assertEquals("http://www.google.com", ZParagraph.ele(1).getText());
		assertEquals("http://www.google.com", ZParagraph.ele(1).getHref().toString());

		ZParagraph = ZParagraph.child(0);
		assertEquals("B: ", ZParagraph.ele(0).toString());
		assertEquals("Google", ZParagraph.ele(1).getText());
		assertTrue(ZParagraph.ele(1).getStyle().getFont().isBold());
		assertEquals("http://www.google.com", ZParagraph.ele(1).getHref().toString());

		ZParagraph = ZParagraph.child(0);
		assertEquals("C: ", ZParagraph.ele(0).toString());
		ZEle media = ZParagraph.ele(1);
		assertEquals("nutz.png", media.getSrc().getFile(media).getName());
		assertEquals("http://nutz.googlecode.com", media.getHref().toString());
	}

	@Test
	public void test_parse_code() throws IOException {
		ZBlock root = root4file("code.txt");
		ZBlock p1 = root.child(0);
		ZBlock code1 = root.child(1);
		ZBlock p2 = root.child(2);
		ZBlock code2 = p2.child(0);
		ZBlock p3 = root.child(3);
		assertEquals("This is the example java code:", p1.toString());
		assertEquals("java", code1.getTitle().toLowerCase());
		assertEquals("public class Abc{\n\tprivate int num;\n\t\t// tt\n}", code1.getText());
		assertEquals("Child:", p2.getText());
		assertEquals("DROP TABLE t_abc;\n\t\t/*t*/", code2.getText());
		assertEquals("-The end-", p3.toString());
	}

	@Test
	public void test_parse_code_with_child() {
		ZBlock root = root("{{{\nB\n}}}\n\tX");
		assertEquals(2, root.size());
		assertEquals("X", root.child(1).getText());
	}

	@Test
	public void test_index_table() {
		ZBlock root = root4file("indexTable_1.txt");
		assertEquals("ABC", root.child(1).getText());
		assertEquals("F", root.child(1).child(0).getText());
		ZBlock index = root.getDoc().buildIndex(IntRange.make("0,1"));
		assertEquals("ABC", index.child(0).getText());
		assertEquals("L1", index.child(1).getText());
		assertEquals("L1.1", index.child(1).child(0).getText());
		assertEquals("L2", index.child(2).getText());
		assertEquals("L2.1", index.child(2).child(0).getText());
		index = root.getDoc().buildIndex(IntRange.make("1,2"));
		assertEquals("L1.1", index.child(0).getText());
		assertEquals("L1.1.1", index.child(0).child(0).getText());
		assertEquals("L2.1", index.child(1).getText());
		assertEquals("L2.1.1", index.child(1).child(0).getText());
	}

	@Test
	public void test_eval_blocks() {
		ZBlock root = root("A\nB\nC\n\nD\n\tD1\nE\n\nF");
		ZBlock[] bs = root.children();
		assertEquals(4, bs.length);

		assertEquals(3, bs[0].size());
		assertEquals("A", bs[0].ele(0).getText());
		assertEquals("\n", bs[0].ele(1).getText());
		assertEquals("B", bs[0].ele(2).getText());
		assertEquals("\n", bs[0].ele(3).getText());
		assertEquals("C", bs[0].ele(4).getText());
		assertEquals("\n", bs[0].ele(5).getText());

		assertEquals(1, bs[1].size());
		assertEquals("D", bs[1].child(0).getText());
		assertEquals("D1", bs[1].child(0).child(0).getText());

		assertEquals(1, bs[2].size());
		assertEquals("E\n", bs[2].child(0).getText());

		assertEquals(1, bs[3].size());
		assertEquals("F", bs[3].child(0).getText());
	}

	@Test
	public void test_tab_at_the_ZParagraph_head() {
		ZBlock root = root("A\n\t\tB");
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
		ZBlock root = root(s);
		assertEquals(0, root.child(0, 0, 0).countMyTypeInAncestors());
		assertEquals(0, root.child(0, 0, 0, 0).countMyTypeInAncestors());
		assertEquals(1, root.child(0, 0, 0, 0, 0).countMyTypeInAncestors());
		assertEquals(1, root.child(0, 0, 0, 0, 0, 0).countMyTypeInAncestors());
	}

	@Test
	public void test_list_item() {
		ZBlock root = root4file("list.txt");
		assertEquals(4, root.size());
		ZBlock[] ps = root.child(0).children();
		ZBlock[] ps2;
		assertEquals(1, ps.length);
		assertTrue(ps[0].isUL());

		ZBlock li = ps[0].child(0);
		assertEquals("a", li.getText());
		ps2 = li.children();
		assertEquals(1, ps2.length);
		assertTrue(ps2[0].isOL());
		assertEquals("a1", ps2[0].child(0).getText());
		assertEquals("a2", ps2[0].child(1).getText());

		li = ps[0].child(1);
		assertEquals("b", li.getText());
		ps2 = li.children();
		assertEquals(1, ps2.length);
		assertTrue(ps2[0].isUL());
		assertEquals("b1", ps2[0].child(0).getText());
		assertEquals("b2", ps2[0].child(1).getText());

		li = ps[0].child(2);
		assertEquals("c", li.getText());
		ps2 = li.children();
		assertEquals(2, ps2.length);
		assertTrue(ps2[0].isUL());
		assertTrue(ps2[1].isOL());
		assertEquals("c1", ps2[0].child(0).getText());
		assertEquals("c2", ps2[1].child(0).getText());

		li = ps[0].child(3);
		assertEquals("d", li.getText());
		ps2 = li.children();
		assertEquals(2, ps2.length);
		assertTrue(ps2[1].isOL());
		assertEquals("d1", ps2[0].child(0).getText());
		assertEquals("d2", ps2[1].child(0).getText());

		ZBlock ZParagraph2 = root.child(1);
		assertEquals(0, ZParagraph2.size());

		assertTrue(root.child(2).isBlank());

		assertEquals("-End-", root.child(3).getText());
	}

	@Test
	public void test_list_as_child_of_ZParagraph() {
		ZBlock root = root("A\n\t* LI\nB");
		ZBlock[] bs = root.children();
		assertEquals(2, bs.length);
		assertTrue(bs[0].isHeading());
		assertEquals("B", bs[1].child(0).getText());
		ZBlock[] bss = bs[0].children();
		assertEquals(1, bss.length);
		assertTrue(bss[0].isUL());
	}

	@Test
	public void test_nesting_list() {
		ZBlock root = root("# A\n\t# B");
		assertEquals("A", root.child(0).getText());
		assertTrue(root.child(0).isOL());

		assertEquals("B", root.child(0).child(0).getText());
		assertTrue(root.child(0).child(0).isOL());

		ZBlock[] ps = root.child(0).children();
		assertEquals(1, ps.length);
		assertTrue(ps[0].isOL());
		assertEquals("B", ps[0].child(0).getText());
	}

	@Test
	public void test_escaping() {
		ZBlock root = root("A`[`B`]`C");
		assertEquals("A[B]C", root.child(0).getText());
	}

	@Test
	public void test_link_with_whitespace() {
		ZBlock root = root("[http://abc.com A B C]");
		assertEquals("http://abc.com", root.child(0).ele(0).getHref().toString());
		assertEquals("A B C", root.child(0).getText());
	}

	@Test
	public void test_hr_1() {
		ZBlock root = root("A\n-----\nB");
		ZBlock[] ps = root.children();
		assertEquals("A", ps[0].child(0).getText());
		assertTrue(ps[1].isHr());
		assertEquals("B", ps[2].child(0).getText());
	}

	@Test
	public void test_hr_2() {
		ZBlock root = root("A\n ----- 	\nB");
		ZBlock[] ps = root.children();
		assertEquals("A", ps[0].child(0).getText());
		assertTrue(ps[1].isHr());
		assertEquals("B", ps[2].child(0).getText());
	}

	@Test
	public void test_heading_with_including() {
		ZBlock root = root("A\n\t#index:3\n\tB");
		assertEquals(1, root.size());
		ZBlock[] ps = root.children();
		assertEquals("A", ps[0].child(0).getText());
		ps = ps[0].child(0).children();
		assertTrue(ps[0].hasIndexRange());
		assertEquals("B", ps[1].child(0).getText());
	}

	@Test
	public void test_one_row_shell() {
		ZBlock root = root("||A11||A12||");
		ZBlock table = root.child(0);
		ZBlock row = table.child(0);
		assertEquals(2, row.size());
		assertEquals("A11", row.child(0).getText());
		assertEquals("A12", row.child(1).getText());
	}

	@Test
	public void test_basic_shell() {
		ZBlock root = root("A\n\t||C11||C12||\n\t||C21||C22||");
		assertEquals("A", root.child(0).getText());
		ZBlock shell = root.child(0, 0);
		assertEquals(2, shell.size());
		ZBlock[] rows = shell.children();
		assertEquals(2, rows.length);
		assertEquals(2, rows[0].size());
		assertEquals(2, rows[1].size());
		assertEquals("C11", rows[0].child(0).getText());
		assertEquals("C22", rows[1].child(1).getText());
	}

	@Test
	public void test_child_of_blank_ZParagraph() {
		ZBlock root = root("A\n\n\tB\n\nC");
		assertEquals("A", root.child(0).getText());
		assertEquals("B", root.child(0).child(0).getText());
		ZBlock[] bs = root.children();
		assertEquals(2, bs.length);
		assertEquals("A", bs[0].child(0).getText());
		assertEquals("C", bs[1].child(0).getText());
	}

	@Test
	public void test_code_with_indent() {
		ZBlock root = root("A\n\t{{{\n\tX\n\n\tY\n\t}}}");
		assertEquals(1, root.size());
		assertEquals("A", root.child(0).getText());
		ZBlock code = root.child(0, 0);
		assertEquals("X\n\nY", code.getText());
	}

	@Test
	public void test_get_block_with_blank() {
		ZBlock root = root("A\n\tA1\n\n\tA2");
		ZBlock[] bs = root.child(0).children();
		assertEquals(2, bs.length);
	}

	@Test
	public void test_nested_inZParagraphs() {
		ZBlock root = root("A[abc.htm {_B B}]C");
		ZEle[] inZParagraphs = root.child(0).eles();
		assertEquals("A", inZParagraphs[0].getText());
		assertEquals("B B", inZParagraphs[1].getText());
		assertEquals("abc.htm", inZParagraphs[1].getHref().toString());
		assertTrue(inZParagraphs[1].getStyle().getFont().isItalic());
		assertEquals("C", inZParagraphs[2].getText());
	}

	@Test
	public void test_inZParagraph_color() {
		ZBlock root = root("A{*#00F;_B}C");
		ZEle[] inZParagraphs = root.child(0).eles();
		assertEquals("A", inZParagraphs[0].getText());
		assertTrue(inZParagraphs[1].getStyle().getFont().hasColor());
		assertEquals("#0000FF", inZParagraphs[1].getStyle().getFont().getColor().toString());
		assertEquals("C", inZParagraphs[2].getText());
	}

	@Test
	public void test_title_author() {
		String s = "#title:TT";
		s = s + "\n#author:A1";
		s = s + "\n#author:A2";
		s = s + "\n#verifier:V1";
		s = s + "\n#verifier:V2";
		s = s + "\n#author:A3";
		s = s + "\n#verifier:V3";

		ZDoc doc = root(s).getDoc();
		assertEquals("A", doc.getTitle());
		assertEquals(3, doc.authors().length);
		assertEquals(3, doc.verifiers().length);
	}

}
