package org.nutz.doc.zdoc;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.doc.meta.ZType;
import org.nutz.lang.Strings;

import static org.nutz.doc.zdoc.ZDocUnits.*;

public class ZDocScanningTest {

	@Test
	public void test_simple() {
		String s = "A";
		s = s + "\nB";
		s = s + "\n\tC";
		s = s + "\n\t\tD";
		s = s + "\nE";
		Line line = scan4(s);
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
		ScanResult sr = sr4(s);
		assertEquals("A", sr.doc().getTitle());
		assertEquals("B", sr.doc().getMeta("author"));
		assertEquals("C", sr.doc().getMeta("verifier"));
		assertEquals("3:5", sr.root().child(0).getIndexRange().toString());
	}

	@Test
	public void test_chinese_title() {
		String s = "#title: 测试 Links \r\n";
		ScanResult sr = sr4(s);
		assertEquals("测试 Links", sr.doc().getMeta("title"));
	}

	@Test
	public void test_uls() {
		String s = "Heading";
		s = s + "\n\t* A";
		s = s + "\n\t\t* A1";
		s = s + "\n\t* B";
		Line line = scan4(s);
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
		Line line = scan4(s);
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
		Line line = scan4(s);
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
		Line line = scan4(s);
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
		Line line = scan4(s);

		assertEquals("Heading", line.child(0).getText());

		assertTrue(line.child(0, 0).isHr());
		assertTrue(line.child(0, 1).isBlank());
		assertTrue(line.child(0, 2).isBlank());
		assertTrue(line.child(0, 3).isHr());
	}

	@Test
	public void test_hr_after_p() {
		String s = "Heading";
		s = s + "\n\tA";
		s = s + "\n\t\tA11";
		s = s + "\n\t-----";
		s = s + "\n-----";
		s = s + "\nB";
		Line line = scan4(s);

		assertEquals("A", line.child(0, 0).getText());
		assertEquals("A11", line.child(0, 0, 0).getText());
		assertTrue(line.child(0, 1).isHr());
		assertTrue(line.child(1).isHr());
		assertEquals("B", line.child(2).getText());
	}

	@Test
	public void test_code() {
		String s = "Heading";
		s = s + "\n\t{{{<JAVA>";
		s = s + "\n\t\tA";
		s = s + "\n\t\tB";
		s = s + "\n\t  C";
		s = s + "\n\t}}}";
		Line line = scan4(s);

		assertEquals("Heading", line.child(0).getText());

		Line code = line.child(0, 0);
		assertEquals("JAVA", code.getCodeType());
		assertEquals(ZType.CODE, code.type);
		assertTrue(code.isCodeStart());

		assertEquals("\tA\n\tB\n  C\n", code.getText());
	}

	@Test
	public void test_join() {
		Line line = new Line("#inde");
		assertTrue(line.isNormal());
		line.join("x:1,6");
		assertNull(line.getIndexRange());
		assertTrue(line.isNormal());
		assertEquals("#index:1,6", line.getText());
	}

	@Test
	public void test_end_by_escaping() {
		String s = "A \\";
		s = s + "\n B\\\\";
		s = s + "\nC\\";
		s = s + "\nD`\\`";
		Line line = scan4(s);

		assertEquals("A B\\CD`\\`", line.child(0).getText());

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
		Line line = scan4(s);

		assertEquals("A", line.child(0).getText());
		assertEquals("B", line.child(0, 0).getText());
		assertEquals("", line.child(0, 0, 0).getText());
		assertEquals("C", line.child(0, 1).getText());
		assertEquals("D", line.child(0, 1, 0).getText());
		assertEquals("", line.child(0, 1, 0, 0).getText());
		assertEquals("E", line.child(0, 1, 1).getText());
		assertEquals("F", line.child(1).getText());
	}

	@Test
	public void test_simple_code_ignore_tab() {
		String s = "code:";
		s = s + "\n\t{{{<JAVA> ";
		s = s + "\n\tA";
		s = s + "\n\tB";
		s = s + "\n\t}}}";
		Line line = scan4(s);

		assertEquals("code:", line.child(0).getText());
		assertEquals("JAVA", line.child(0, 0).getCodeType());
		assertTrue(line.child(0, 0).isCodeStart());
	}

	@Test
	public void test_simple_heading_structure() {
		String s = "A";
		s = s + "\n\tB";
		s = s + "\n\t\t111";
		s = s + "\n";
		s = s + "\n\tC";
		s = s + "\n\t\t222";
		Line line = scan4(s);

		Line a = line.child(0);
		assertEquals("B", a.child(0).getText());
		assertEquals("111", a.child(0, 0).getText());
		assertTrue(a.child(0, 0, 0).isBlank());
		assertEquals("C", a.child(1).getText());
		assertEquals("222", a.child(1, 0).getText());
	}

	@Test
	public void test_li_after_p() {
		String s = "A";
		s = s + "\n\tB";
		s = s + "\n\t* UL1";
		Line line = scan4(s);

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
		Line line = scan4(s);

		assertEquals("A", line.child(0).getText());
		assertEquals("B", line.child(0, 0).getText());
		assertEquals("111", line.child(0, 0, 0).getText());
		assertTrue(line.child(0, 0, 0, 0).isBlank());
		assertTrue(line.child(1).isHr());
		assertEquals("hhh", line.child(2).getText());
		assertEquals("C", line.child(2, 0).getText());
		assertEquals("222", line.child(2, 0, 0).getText());
	}

	@Test
	public void tet_hr_order_issue() {
		String s = "A";
		s = s + "\n\tA1";
		s = s + "\n\t-------------";
		s = s + "\n\t\t\tA2";
		Line line = scan4(s);

		assertEquals("A", line.child(0).getText());
		assertEquals("A1", line.child(0, 0).getText());
		assertTrue(line.child(0, 1).isHr());
		assertEquals("A2", line.child(0, 2).getText());
	}

	@Test
	public void test_quick_index() {
		String s = "#index:3";
		Line line = scan4(s);

		assertEquals(0, line.child(0).getIndexRange().getLeft());
		assertEquals(3, line.child(0).getIndexRange().getRight());
	}

	@Test
	public void test_escape_ul_with_child() {
		String s = "* A\\";
		s = s + "\nB";
		s = s + "\n\t * A1";
		Line line = scan4(s);

		assertEquals("AB", line.child(0).getText());
		assertTrue(line.child(0).isULI());
		assertEquals("A1", line.child(0, 0).getText());
		assertTrue(line.child(0, 0).isULI());
	}

	@Test
	public void code_same_level_with_paragraph() {
		String s = "A";
		s = s + "\n{{{";
		s = s + "\nX";
		s = s + "\n}}}";
		Line line = scan4(s);

		assertEquals("A", line.child(0).getText());
		assertTrue(line.child(1).isCodeStart());
		assertEquals("X\n", line.child(1).getText());
		assertEquals(2, line.children().size());
	}

	@Test
	public void code_with_child_indent() {
		String s = "Heading";
		s = s + "\n\t{{{<SQL>";
		s = s + "\n\tDDD";
		s = s + "\n\t}}}";
		s = s + "\n\t\tTT";
		Line line = scan4(s);

		assertEquals("Heading", line.child(0).getText());
		assertEquals("SQL", line.child(0, 0).getCodeType());
		assertEquals("DDD\n", line.child(0, 0).getText());
		assertEquals("TT", line.child(0, 1).getText());

	}

	@Test
	public void heading_with_blank_child() {
		String s = "A";
		s = s + "\n";
		s = s + "\n\tB";
		Line line = scan4(s);

		assertEquals("A", line.child(0).getText());
		assertTrue(line.child(0, 0).isBlank());
		assertEquals("B", line.child(0, 1).getText());
	}

}
