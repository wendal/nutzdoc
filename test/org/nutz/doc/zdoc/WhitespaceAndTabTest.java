package org.nutz.doc.zdoc;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import static org.nutz.doc.zdoc.ZDocUnits.*;

public class WhitespaceAndTabTest {

	@Test
	public void test_all_4whitespace() {
		String s = "A";
		s = s + "\nB";
		s = s + "\n    C";
		s = s + "\n        D";
		s = s + "\nE";
		Line line = scan4(s);
		assertEquals("A", line.child(0).getText());
		assertEquals("B", line.child(1).getText());
		assertEquals("C", line.child(1, 0).getText());
		assertEquals("D", line.child(1, 0, 0).getText());
		assertEquals("E", line.child(2).getText());

	}

	@Test
	public void test_all_2whitespace() {
		String s = "A";
		s = s + "\nB";
		s = s + "\n  C";
		s = s + "\n    D";
		s = s + "\nE";
		Line line = scan2(s);
		assertEquals("A", line.child(0).getText());
		assertEquals("B", line.child(1).getText());
		assertEquals("C", line.child(1, 0).getText());
		assertEquals("D", line.child(1, 0, 0).getText());
		assertEquals("E", line.child(2).getText());
	}

	@Test
	public void test_tab_and_4whitespace() {
		String s = "A";
		s = s + "\nB";
		s = s + "\n    C";
		s = s + "\n  \t    D";
		s = s + "\nE";
		Line line = scan4(s);
		assertEquals("A", line.child(0).getText());
		assertEquals("B", line.child(1).getText());
		assertEquals("C", line.child(1, 0).getText());
		assertEquals("D", line.child(1, 0, 0).getText());
		assertEquals("E", line.child(2).getText());

	}

	@Test
	public void test_tab_and_2whitespace() {
		String s = "A";
		s = s + "\nB";
		s = s + "\n  C";
		s = s + "\n \t\tD";
		s = s + "\nE";
		Line line = scan2(s);
		assertEquals("A", line.child(0).getText());
		assertEquals("B", line.child(1).getText());
		assertEquals("C", line.child(1, 0).getText());
		assertEquals("D", line.child(1, 0, 0).getText());
		assertEquals("E", line.child(2).getText());
	}

	@Test
	public void test_tab_and_2whitespace2() {
		String s = "A";
		s = s + "\nB";
		s = s + "\n  C";
		s = s + "\n \t \tD";
		s = s + "\nE";
		Line line = scan2(s);
		assertEquals("A", line.child(0).getText());
		assertEquals("B", line.child(1).getText());
		assertEquals("C", line.child(1, 0).getText());
		assertEquals("D", line.child(1, 0, 0).getText());
		assertEquals("E", line.child(2).getText());
	}
}
