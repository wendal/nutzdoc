package org.nutz.doc.zdoc;

import java.util.Calendar;

import org.junit.Test;
import org.nutz.castor.Castors;
import org.nutz.doc.meta.ZBlock;

import static org.junit.Assert.*;
import static org.nutz.doc.zdoc.ZDocUnits.*;

public class VarContextParserTest {

	@Test
	public void test_var_title() {
		String s = "#title:ABC";
		s += "\nHeading";
		s += "\n    A: {*${title}}";
		s += "\n";
		s += "\n    B: [http://nutzam.com ${title}]";

		ZBlock root = root(s);
		assertEquals("ABC", root.getDoc().getTitle());
		assertEquals("ABC", root.desc(0, 0).ele(1).getText());
		assertTrue(root.desc(0, 0).ele(1).getStyle().font().isBold());

		assertEquals("nutzam.com", root.desc(0, 1).ele(1).getHref().getValue());
		assertEquals("ABC", root.desc(0, 1).ele(1).getText());
	}

	@Test
	public void test_var_now() {
		String now = Castors.me().castToString(Calendar.getInstance());
		now = now.substring(0, now.indexOf(' '));

		String s = "#title:ABC";
		s += "\nHeading";
		s += "\n    A: {*${now}}";

		ZBlock root = root(s);
		assertTrue(root.desc(0, 0).ele(1).getText().startsWith(now));
		assertTrue(root.desc(0, 0).ele(1).getStyle().font().isBold());
	}

}
