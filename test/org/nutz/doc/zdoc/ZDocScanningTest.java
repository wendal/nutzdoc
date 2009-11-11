package org.nutz.doc.zdoc;

import static org.junit.Assert.*;

import java.io.BufferedReader;

import org.junit.Test;
import org.nutz.doc.meta.ZType;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

public class ZDocScanningTest {

	private static Line scan(String s) {
		return new ZDocScanning().scan(new BufferedReader(Lang.inr(s)));
	}

	@Test
	public void test_simple() {
		String s = "A";
		s = s + "\nB";
		s = s + "\n\tC";
		s = s + "\n\t\tD";
		s = s + "\nE";
		Line line = scan(s);
		String s2 = line.toString();
		assertEquals(s, Strings.trim(s2));
		assertEquals("A", line.child(0).getText());
		assertEquals("B", line.child(1).getText());
		assertEquals("C", line.child(1, 0).getText());
		assertEquals("D", line.child(1, 0, 0).getText());
		assertEquals("E", line.child(2).getText());
	}

	@Test
	public void test_title_author_verifier() {
		String s = "#title:A";
		s = s + "\n#author:B";
		s = s + "\n#verifier:C";
		s = s + "\n#index:3,5";
		Line line = scan(s);
		String s2 = line.toString();
		assertEquals(s, Strings.trim(s2));
		assertEquals("A", line.child(0).getTitle());
		assertEquals("B", line.child(1).getAuthor().toString());
		assertEquals("C", line.child(2).getVerifier().toString());
		assertEquals("3,5", line.child(3).getIndexRange().toString());
	}

	@Test
	public void test_uls() {
		String s = "Heading";
		s = s + "\n\t* A";
		s = s + "\n\t\t* A1";
		s = s + "\n\t* B";
		Line line = scan(s);
		String s2 = line.toString();
		assertEquals(s, Strings.trim(s2));
		assertEquals("Heading", line.child(0).getText());

		assertEquals("A", line.child(0, 0).getText());
		assertTrue(line.child(0, 0).isULI());

		assertEquals("A1", line.child(0, 0, 0).getText());
		assertTrue(line.child(0, 0, 0).isULI());

		assertEquals("B", line.child(0, 1).getText());
		assertTrue(line.child(0, 1).isULI());
	}

	@Test
	public void test_ols() {
		String s = "Heading";
		s = s + "\n\t# A";
		s = s + "\n\t\t# A1";
		s = s + "\n\t# B";
		Line line = scan(s);
		String s2 = line.toString();

		assertEquals(s, Strings.trim(s2));
		assertEquals("Heading", line.child(0).getText());

		assertEquals("A", line.child(0, 0).getText());
		assertTrue(line.child(0, 0).isOLI());

		assertEquals("A1", line.child(0, 0, 0).getText());
		assertTrue(line.child(0, 0, 0).isOLI());

		assertEquals("B", line.child(0, 1).getText());
		assertTrue(line.child(0, 1).isOLI());
	}

	@Test
	public void test_ul_ols() {
		String s = "Heading";
		s = s + "\n\t# A";
		s = s + "\n\t\t* A1";
		s = s + "\n\t\t\t# A11";
		s = s + "\n\t\t\t* A12";
		s = s + "\n\t# B";
		Line line = scan(s);
		String s2 = line.toString();

		assertEquals(s, Strings.trim(s2));
		assertEquals("Heading", line.child(0).getText());

		assertEquals("A", line.child(0, 0).getText());
		assertTrue(line.child(0, 0).isOLI());

		assertEquals("A1", line.child(0, 0, 0).getText());
		assertTrue(line.child(0, 0, 0).isULI());

		assertEquals("A11", line.child(0, 0, 0, 0).getText());
		assertTrue(line.child(0, 0, 0, 0).isOLI());

		assertEquals("A12", line.child(0, 0, 0, 1).getText());
		assertTrue(line.child(0, 0, 0, 1).isULI());

		assertEquals("B", line.child(0, 1).getText());
		assertTrue(line.child(0, 1).isOLI());
	}

	@Test
	public void test_table() {
		String s = "Heading";
		s = s + "\n\t||A||B||";
		s = s + "\n\t||C||D||";
		Line line = scan(s);
		String s2 = line.toString();

		assertEquals(s, Strings.trim(s2));
		assertEquals("Heading", line.child(0).getText());

		assertEquals("||A||B||", line.child(0, 0).getText());
		assertTrue(line.child(0, 0).isRow());

		assertEquals("||C||D||", line.child(0, 1).getText());
		assertTrue(line.child(0, 1).isRow());
	}

	@Test
	public void test_hr_and_blank() {
		String s = "Heading";
		s = s + "\n\t-----";
		s = s + "\n\t  ";
		s = s + "\n\t \t \t ";
		s = s + "\n\t  \t ----- \t ";
		Line line = scan(s);

		assertEquals("Heading", line.child(0).getText());

		assertTrue(line.child(1).isHr());
		assertTrue(line.child(2).isBlank());
		assertTrue(line.child(3).isBlank());
		assertTrue(line.child(4).isHr());
	}

	@Test
	public void test_hr_after_p() {
		String s = "Heading";
		s = s + "\n\tA";
		s = s + "\n\t\tA11";
		s = s + "\n\t-----";
		s = s + "\n-----";
		s = s + "\nB";
		Line line = scan(s);

		assertEquals("A", line.child(0, 0).getText());
		assertEquals("A11", line.child(0, 0, 0).getText());
		assertTrue(line.child(0, 0, 1).isHr());
		assertTrue(line.child(0, 0, 2).isHr());
		assertEquals("B", line.child(1).getText());
	}

	@Test
	public void test_code() {
		String s = "Heading";
		s = s + "\n\t{{{<JAVA>";
		s = s + "\n\t\tA";
		s = s + "\n\t\tB";
		s = s + "\n\tC";
		s = s + "\n\t}}}";
		Line line = scan(s);

		assertEquals("Heading", line.child(0).getText());

		assertEquals("JAVA", line.child(0, 0).getCodeType());
		assertEquals(ZType.CODE, line.child(0, 0).type);
		assertTrue(line.child(0, 0).isCodeStart());

		assertEquals("A", line.child(0, 0, 0).getText());
		assertEquals("B", line.child(0, 0, 1).getText());
		assertEquals("C", line.child(0, 1).getText());

		assertTrue(line.child(0, 2).isCodeEnd());
	}

	@Test
	public void test_join() {
		Line line = Line.make("#inde");
		assertTrue(line.isNormal());
		line.join("x:1,6");
		assertTrue(line.getIndexRange().in(4));
	}

	@Test
	public void test_end_by_escaping() {
		String s = "A\\";
		s = s + "\nB\\\\";
		s = s + "\nC\\ \t ";
		s = s + "\nD`\\`";
		Line line = scan(s);

		assertTrue(line.child(0).isEndByEscaping());
		assertTrue(line.child(1).isEndByEscaping());
		assertTrue(line.child(2).isEndByEscaping());
		assertFalse(line.child(3).isEndByEscaping());
		assertEquals("D`\\`", line.child(3).getText());

	}

	@Test
	public void test_blank_line() {
		String s = "A";
		s = s + "\n\tB";
		s = s + "\n";
		s = s + "\n\tC";
		s = s + "\n\t\tD";
		s = s + "\n";
		s = s + "\n\t\tE";
		s = s + "\nF";
		Line line = scan(s);

		assertEquals("A", line.child(0).getText());
		assertEquals("B", line.child(0, 0).getText());
		assertEquals("", line.child(0, 1).getText());
		assertEquals("C", line.child(0, 2).getText());
		assertEquals("D", line.child(0, 2, 0).getText());
		assertEquals("", line.child(0, 2, 1).getText());
		assertEquals("E", line.child(0, 2, 2).getText());
		assertEquals("F", line.child(1).getText());
	}

	@Test
	public void test_simple_code_ignore_tab() {
		String s = "code:";
		s = s + "\n\t{{{<JAVA> ";
		s = s + "\n\tA";
		s = s + "\n\tB";
		s = s + "\n\t}}}";
		Line line = scan(s);

		assertEquals("code:", line.child(0).getText());
		assertEquals("JAVA", line.child(0, 0).getCodeType());
		assertTrue(line.child(0, 0).isCodeStart());
		assertTrue(line.child(0, 3).isCodeEnd());
	}

	@Test
	public void test_simple_heading_structure() {
		String s = "A";
		s = s + "\n\tB";
		s = s + "\n\t\t111";
		s = s + "\n";
		s = s + "\n\tC";
		s = s + "\n\t\t222";
		Line line = scan(s);

		Line a = line.child(0);
		assertEquals("B", a.child(0).getText());
		assertEquals("111", a.child(0, 0).getText());
		assertTrue(a.child(0, 1).isBlank());
		assertEquals("C", a.child(1).getText());
		assertEquals("222", a.child(1, 0).getText());
	}

	@Test
	public void test_li_after_p() {
		String s = "A";
		s = s + "\n\tB";
		s = s + "\n\t* UL1";
		Line line = scan(s);

		assertEquals("B", line.child(0, 0).getText());
		assertTrue(line.child(0, 1).isULI());
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
		Line line = scan(s);

		assertEquals("A", line.child(0).getText());
		assertEquals("B", line.child(0, 0).getText());
		assertEquals("111", line.child(0, 0, 0).getText());
		assertTrue(line.child(0, 0, 1).isBlank());
		assertTrue(line.child(0, 0, 2).isHr());
		assertEquals("hhh", line.child(0, 0, 3).getText());
		assertEquals("C", line.child(0, 1).getText());
		assertEquals("222", line.child(0, 1, 0).getText());
	}

	@Test
	public void tet_hr_order_issue() {
		String s = "A";
		s = s + "\n\tA1";
		s = s + "\n-------------";
		s = s + "\n\t\t\tA2";
		Line line = scan(s);

		assertEquals("A", line.child(0).getText());
		assertEquals("A1", line.child(0, 0).getText());
		assertTrue(line.child(0, 1).isHr());
		assertEquals("A2", line.child(0, 2).getText());
	}

	@Test
	public void test_quick_index() {
		String s = "#index:3";
		Line line = scan(s);

		assertEquals(0, line.child(0).getIndexRange().getLeft());
		assertEquals(3, line.child(0).getIndexRange().getRight());
	}

	@Test
	public void test_escape_ul_with_child() {
		String s = "* A\\";
		s = s + "\nB";
		s = s + "\n\t * A1";
		Line line = scan(s);

		assertEquals("A", line.child(0).getText());
		assertTrue(line.child(0).isULI());
		assertTrue(line.child(0).isEndByEscaping());
		assertEquals("B", line.child(1).getText());
		assertEquals("A1", line.child(1, 0).getText());
		assertTrue(line.child(1, 0).isULI());
	}

	@Test
	public void code_same_level_with_paragraph() {
		String s = "A";
		s = s + "\n{{{";
		s = s + "\nX";
		s = s + "\n}}}";
		Line line = scan(s);

		assertEquals("A", line.child(0).getText());
		assertTrue(line.child(1).isCodeStart());
		assertEquals("X", line.child(2).getText());
		assertTrue(line.child(3).isCodeEnd());
	}

	@Test
	public void heading_with_blank_child() {
		String s = "A";
		s = s + "\n";
		s = s + "\n\tB";
		Line line = scan(s);

		assertEquals("A", line.child(0).getText());
		assertEquals("B", line.child(0, 0).getText());
	}
}
