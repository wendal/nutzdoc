package org.nutz.doc.style;

import static org.junit.Assert.*;

import org.junit.Test;

public class ColorTest {

	@Test
	public void test_parse_color_ffffff() {
		ZColor c = new ZColor("FFFFFF");
		assertEquals(255, c.getRed());
		assertEquals(255, c.getGreen());
		assertEquals(255, c.getBlue());
		assertEquals("#FFFFFF", c.toString());
	}
	
	@Test
	public void test_parse_color_ABC() {
		ZColor c = new ZColor("#ABC");
		assertEquals(170, c.getRed());
		assertEquals(187, c.getGreen());
		assertEquals(204, c.getBlue());
		assertEquals("#AABBCC", c.toString());
	}
	
	@Test
	public void test_parse_color_00F() {
		ZColor c = new ZColor("#00F");
		assertEquals(0, c.getRed());
		assertEquals(0, c.getGreen());
		assertEquals(255, c.getBlue());
		assertEquals("#0000FF", c.toString());
	}

}
