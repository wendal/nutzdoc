package org.nutz.doc.zdoc;

import org.junit.Test;
import org.nutz.doc.meta.ZDoc;

import static org.junit.Assert.*;
import static org.nutz.doc.zdoc.ZDocUnits.*;

public class MetaScanningTest {

	@Test
	public void test_simple_meta() {
		String s = "A";
		s = s + "\nB";
		s = s + "\n  C";
		s = s + "\n    D";
		s = s + "\n#abc:U";
		s = s + "\n    E";
		s = s + "\n#title:Haha";
		s = s + "\n#abc:Z";

		ScanResult sr = sr2(s);
		Line root = sr.root();
		assertEquals("A", root.child(0).getText());
		assertEquals("B", root.child(1).getText());
		assertEquals("C", root.child(1, 0).getText());
		assertEquals("D", root.child(1, 0, 0).getText());
		assertEquals("E", root.child(1, 0, 1).getText());

		ZDoc doc = sr.doc();
		assertEquals("Z", doc.getMeta("abc"));
		assertEquals("U", doc.getMetaList("abc").get(0));
		assertEquals("Z", doc.getMetaList("abc").get(1));
		assertEquals("Haha", doc.getMeta("title"));
		assertEquals("Haha", doc.getTitle());
	}

}
