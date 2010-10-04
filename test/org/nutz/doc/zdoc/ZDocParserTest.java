package org.nutz.doc.zdoc;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.nutz.doc.meta.ZBlock;
import org.nutz.doc.meta.ZDoc;
import org.nutz.doc.meta.ZEle;
import org.nutz.doc.meta.ZIndex;
import org.nutz.lang.util.IntRange;
import org.nutz.lang.util.Node;

import static org.nutz.doc.zdoc.ZDocUnits.*;

public class ZDocParserTest {

	@Test
	public void test_doc_title() {
		String s = "#title:abc\n";
		s = s + "ddd";
		ZDoc doc = doc(s);
		assertEquals("abc", doc.getTitle());
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
		assertEquals("a", cellD.ele(1).getHref().getValue());
		assertEquals("E", cellD.ele(2).getText());
	}

	@Test
	public void test_simple_iterator1() {
		String s = "A";
		s = s + "\n\tB";

		ZBlock root = root(s);
		Iterator<ZBlock> it = root.iterator();
		assertEquals("A", it.next().getText());
		assertEquals("B", it.next().getText());
		assertNull(it.next());
		assertFalse(it.hasNext());
	}

	@Test
	public void test_simple_iterator2() {
		String s = "A";
		s = s + "\n\tB";
		s = s + "\n";
		s = s + "\n\tC";
		s = s + "\n\t\tD";
		s = s + "\n";
		s = s + "\n\t\tE";
		s = s + "\nF";

		ZBlock root = root(s);

		Iterator<ZBlock> it = root.iterator();
		assertEquals("A", it.next().getText());
		assertEquals("B", it.next().getText());
		assertTrue(it.next().isBlank());
		assertEquals("C", it.next().getText());
		assertEquals("D", it.next().getText());
		assertTrue(it.next().isBlank());
		assertEquals("E", it.next().getText());
		assertEquals("F", it.next().getText());
		assertNull(it.next());
		assertFalse(it.hasNext());
	}

	@Test
	public void test_get_root_images() {
		String s = "A";
		s = s + "\n\tB<a.png>";
		s = s + "\n";
		s = s + "\n\tC<b.png>ddd<c.png>";
		s = s + "\n\t\tD";
		s = s + "\n";
		s = s + "\n\t\tE";
		s = s + "\nF";

		ZBlock root = root(s);
		List<ZEle> images = root.getImages();
		assertEquals(3, images.size());
		assertEquals("a.png", images.get(0).getSrc().getValue());
		assertEquals("b.png", images.get(1).getSrc().getValue());
		assertEquals("c.png", images.get(2).getSrc().getValue());
	}

	@Test
	public void test_get_root_links() {
		String s = "A";
		s = s + "\n\tB[a.png]";
		s = s + "\n";
		s = s + "\n\tC[b.png]ddd[c.png]";
		s = s + "\n\t\tD";
		s = s + "\n";
		s = s + "\n\t\tE";
		s = s + "\nF";

		ZBlock root = root(s);
		List<ZEle> images = root.getLinks();
		assertEquals(3, images.size());
		assertEquals("a.png", images.get(0).getHref().getValue());
		assertEquals("b.png", images.get(1).getHref().getValue());
		assertEquals("c.png", images.get(2).getHref().getValue());
	}

	@Test
	public void test_normal_ul() {
		String s = "A";
		s = s + "\n\t* L1";
		s = s + "\n\t* L2";

		ZBlock root = root(s);
		ZBlock ul = root.desc(0, 0);
		assertEquals(2, ul.size());
		assertEquals("L1", ul.child(0).getText());
		assertEquals("L2", ul.child(1).getText());
	}

	@Test
	public void test_ul_with_blank() {
		String s = "A";
		s = s + "\n\t* L1";
		s = s + "\n";
		s = s + "\n\t* L2";

		ZBlock root = root(s);
		ZBlock ul = root.desc(0, 0);
		assertEquals(2, ul.size());
		assertEquals("L1", ul.child(0).getText());
		assertEquals("L2", ul.child(1).getText());
	}

	@Test
	public void test_ul_ol_nesting() {
		String s = "A";
		s = s + "\n\t* L1";
		s = s + "\n\t\t # O1";
		s = s + "\n\t\t # O2";
		s = s + "\n\t* L2";

		ZBlock root = root(s);
		ZBlock ul = root.desc(0, 0);
		assertEquals(2, ul.size());
		assertEquals("L1", ul.child(0).getText());
		assertEquals("L2", ul.child(1).getText());
		assertEquals("O1", ul.desc(0, 0, 0).getText());
		assertEquals("O2", ul.desc(0, 0, 1).getText());
	}

	@Test
	public void test_simple_code() {
		String s = "{{{<JAVA> ";
		s = s + "\n\tA";
		s = s + "\n\tB";
		s = s + "\n}}}";

		ZBlock root = root(s);
		ZBlock code = root.child(0);
		assertEquals("JAVA", code.getTitle());
		assertEquals("\tA\n\tB\n", code.getText());
	}

	@Test
	public void test_simple_code_ignore_tab() {
		String s = "code:";
		s = s + "\n\t{{{<JAVA> ";
		s = s + "\n\tA";
		s = s + "\n\tB";
		s = s + "\n\t}}}";

		ZBlock root = root(s);
		ZBlock code = root.desc(0, 0);
		assertEquals("JAVA", code.getTitle());
		assertEquals("A\nB\n", code.getText());
	}

	@Test
	public void test_simple_heading_structure() {
		String s = "A";
		s = s + "\n\tB";
		s = s + "\n\t\t111";
		s = s + "\n";
		s = s + "\n\tC";
		s = s + "\n\t\t222";

		ZBlock root = root(s);
		ZBlock a = root.child(0);
		assertEquals("B", a.child(0).getText());
		assertEquals("C", a.child(1).getText());
	}

	@Test
	public void test_simple_heading_index() {
		String s = "A";
		s = s + "\n\tB";
		s = s + "\n\t\t111";
		s = s + "\n";
		s = s + "\n\tC";
		s = s + "\n\t\t222";

		ZBlock root = root(s);
		Node<ZIndex> is = root.buildIndex(IntRange.make(0, 3));
		assertEquals("A", is.desc(0).get().getText());
		assertEquals("1", is.desc(0).get().getNumberString());

		assertEquals("B", is.desc(0, 0).get().getText());
		assertEquals("1.1", is.desc(0, 0).get().getNumberString());

		assertEquals("C", is.desc(0, 1).get().getText());
		assertEquals("1.2", is.desc(0, 1).get().getNumberString());
	}

	@Test
	public void test_li_after_p() {
		String s = "A";
		s = s + "\n\tB";
		s = s + "\n\t* UL1";

		ZBlock root = root(s);
		assertTrue(root.desc(0, 1).isUL());
		assertTrue(root.desc(0, 1, 0).isULI());
	}

	@Test
	public void test_simple_title() {
		String s = "#title:A";
		s = s + "\nB";
		s = s + "\n\t* UL1";

		ZBlock root = root(s);
		assertEquals("A", root.getDoc().getTitle());

		Iterator<ZBlock> it = root.iterator();
		ZBlock next = it.next();
		assertEquals("B", next.getText());
		next = it.next();
		assertTrue(next.isUL());
		next = it.next();
		assertEquals("UL1", next.getText());
		next = it.next();
		assertNull(next);

	}

	@Test
	public void test_hr_in_heading() {
		String s = "A";
		s = s + "\n\tB";
		s = s + "\n\t\t111";
		s = s + "\n";
		s = s + "\n-------------";
		s = s + "\n\t\thhh";
		s = s + "\n\tC";
		s = s + "\n\t\t222";

		ZBlock root = root(s);
		assertEquals("A", root.desc(0).getText());
		assertEquals("B", root.desc(0, 0).getText());
		assertEquals("111", root.desc(0, 0, 0).getText());
		assertTrue(root.desc(1).isHr());
		assertEquals("hhh", root.desc(2).getText());
		assertEquals("C", root.desc(2, 0).getText());
		assertEquals("222", root.desc(2, 0, 0).getText());
	}

	@Test
	public void tet_hr_order_issue() {
		String s = "A";
		s = s + "\n\tA1";
		s = s + "\n-------------";
		s = s + "\n\t\t\tA2";

		ZBlock root = root(s);

		assertEquals("A", root.desc(0).getText());
		assertEquals("A1", root.desc(0, 0).getText());
		assertTrue(root.desc(1).isHr());
		assertEquals("A2", root.desc(2).getText());
	}

	@Test
	public void test_build_index_1() {
		String s = "#title:Test 1";
		s = s + "\n#index:0,1";
		s = s + "\nA";
		s = s + "\n\tA1";
		s = s + "\n\t\tA11";
		s = s + "\n-------------";
		s = s + "\n\t\t\tXXXX  ";
		s = s + "\nB";
		s = s + "\n\tB1";

		ZBlock root = root(s);
		Node<ZIndex> is = root.buildIndex(IntRange.make(0, 1));
		assertEquals(2, is.countChildren());
		Iterator<Node<ZIndex>> it = is.iterator();
		ZIndex index = it.next().get();
		assertEquals("A", index.getText());
		assertEquals("1", index.getNumberString());

		index = it.next().get();
		assertEquals("A1", index.getText());
		assertEquals("1.1", index.getNumberString());

		index = it.next().get();
		assertEquals("XXXX  B", index.getText());
		assertEquals("2", index.getNumberString());

		assertFalse(it.hasNext());
		assertNull(it.next());
	}

	@Test
	public void test_build_index_2() {
		String s = "#title:Test 1";
		s = s + "\n#index:0,1";
		s = s + "\nA";
		s = s + "\n\tA1";
		s = s + "\n\t\tA11";
		s = s + "\n\t\t\tXXXX";
		s = s + "\nB";
		s = s + "\n\tB1";

		ZBlock root = root(s);
		Node<ZIndex> is = root.buildIndex(IntRange.make(0, 2));
		assertEquals(2, is.countChildren());
		Iterator<Node<ZIndex>> it = is.iterator();
		ZIndex index = it.next().get();
		assertEquals("A", index.getText());
		assertEquals("1", index.getNumberString());

		index = it.next().get();
		assertEquals("A1", index.getText());
		assertEquals("1.1", index.getNumberString());

		index = it.next().get();
		assertEquals("A11", index.getText());
		assertEquals("1.1.1", index.getNumberString());

		index = it.next().get();
		assertEquals("B", index.getText());
		assertEquals("2", index.getNumberString());

		assertFalse(it.hasNext());
		assertNull(it.next());
	}

	@Test
	public void test_escape_ul_with_child() {
		String s = "* A\\";
		s = s + "\nB";
		s = s + "\n\t * A1";

		ZBlock root = root(s);
		assertEquals("AB", root.desc(0, 0).getText());
		assertTrue(root.desc(0, 0).isULI());
		assertEquals("A1", root.desc(0, 0, 0, 0).getText());
		assertTrue(root.desc(0, 0, 0, 0).isULI());
	}

	@Test
	public void code_same_level_with_paragraph() {
		String s = "A";
		s = s + "\n{{{";
		s = s + "\nX";
		s = s + "\n}}}";

		ZBlock root = root(s);
		assertEquals("A", root.desc(0).getText());
		assertEquals("X\n", root.desc(1).getText());
		assertTrue(root.desc(1).isCode());
	}

	@Test
	public void item_end_by_escaping_with_indent_child() {
		String s = "* A\\";
		s = s + "\n\t\tB";

		ZBlock root = root(s);
		assertEquals("AB", root.desc(0, 0).getText());
		assertFalse(root.desc(0, 0).hasChildren());
	}

	@Test
	public void item_end_by_escaping_with_indent_child2() {
		String s = " * A \\";
		s = s + "\n\t\t B\\";
		s = s + "\n\t\t C";
		s = s + "\n\t\t D";

		ZBlock root = root(s);
		assertEquals("A BC", root.desc(0, 0).getText());
		assertTrue(root.desc(0, 0).hasChildren());
		assertEquals("D", root.desc(0, 0, 0).getText());
	}

	@Test
	public void text_end_by_escaping_with_indent_child() {
		String s = "A\\";
		s = s + "\n\t\t B";

		ZBlock root = root(s);
		assertEquals("AB", root.desc(0).getText());
		assertFalse(root.desc(0).hasChildren());
	}

	@Test
	public void text_end_by_escaping_with_indent_child2() {
		String s = "A\\";
		s = s + "\n\t\t B\\";
		s = s + "\n\t\t C";
		s = s + "\n\t\t D";

		ZBlock root = root(s);
		assertEquals("ABC", root.desc(0).getText());
		assertTrue(root.desc(0).hasChildren());
		assertEquals("D", root.desc(0, 0).getText());
	}

}
