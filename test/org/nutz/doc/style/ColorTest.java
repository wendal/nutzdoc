package org.nutz.doc.style;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.doc.meta.ZColor;

import static org.nutz.doc.meta.ZD.*;

public class ColorTest {

	@Test
	public void test_parse_color_ffffff() {
		ZColor c = color("FFFFFF");
		assertEquals(255, c.getRed());
		assertEquals(255, c.getGreen());
		assertEquals(255, c.getBlue());
		assertEquals("#FFFFFF", c.toString());
	}

	@Test
	public void test_parse_color_ABC() {
		ZColor c = color("#ABC");
		assertEquals(170, c.getRed());
		assertEquals(187, c.getGreen());
		assertEquals(204, c.getBlue());
		assertEquals("#AABBCC", c.toString());
	}

	@Test
	public void test_parse_color_00F() {
		ZColor c = color("#00F");
		assertEquals(0, c.getRed());
		assertEquals(0, c.getGreen());
		assertEquals(255, c.getBlue());
		assertEquals("#0000FF", c.toString());
	}

}
