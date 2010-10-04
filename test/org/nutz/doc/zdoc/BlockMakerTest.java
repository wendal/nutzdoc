package org.nutz.doc.zdoc;

import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;
import org.nutz.doc.meta.ZBlock;
import org.nutz.doc.meta.ZEle;
import org.nutz.doc.meta.ZRefer;
import org.nutz.json.Json;
import org.nutz.lang.util.Context;

public class BlockMakerTest {

	private static ZBlock P(String s) {
		return new BlockMaker(null, s.toCharArray()).make();
	}

	@SuppressWarnings("unchecked")
	private static ZBlock B(String context, String s) {
		Map<String, Object> map = (Map<String, Object>) Json.fromJson("{" + context + "}");
		return new BlockMaker(new Context().putAll(map), s.toCharArray()).make();
	}

	@Test
	public void test_simple_var_case() {
		ZBlock p = B("A:'a',B:'b'", "A${A}B${B}");
		assertEquals(1, p.eles().length);
		assertEquals("AaBb", p.getText());
	}

	@Test
	public void test_nesting_var_case() {
		ZBlock p = B("A:'a',B:'b'", "A{*${A}}B${B}");
		assertEquals(3, p.eles().length);
		assertEquals("AaBb", p.getText());
		assertTrue(p.ele(1).getStyle().font().isBold());
		assertEquals("a", p.ele(1).getText());
	}

	@Test
	public void very_simple_case() {
		ZBlock p = P("A{*B}C");
		ZEle[] eles = p.eles();
		assertEquals(3, eles.length);
		assertEquals("A", eles[0].getText());
		assertFalse(eles[0].hasStyle());

		assertEquals("B", eles[1].getText());
		assertTrue(eles[1].getStyle().getFont().isBold());
		assertTrue(eles[1].getStyle().getFont().isNormal());
		assertFalse(eles[1].getStyle().getFont().isItalic());
		assertFalse(eles[1].getStyle().getFont().isSub());

		assertEquals("C", eles[2].getText());
		assertFalse(eles[2].hasStyle());
	}

	@Test
	public void test_simple_image() {
		ZBlock p = P("<b.png>");
		assertTrue(p.ele(0).isImage());
		assertEquals("b.png", p.ele(0).getSrc().getValue());

	}

	@Test
	public void test_image_sizing() {
		ZBlock p = P("<3x5:b.png>");
		assertTrue(p.ele(0).isImage());
		assertEquals("b.png", p.ele(0).getSrc().getValue());
		assertEquals(3, p.ele(0).getWidth());
		assertEquals(5, p.ele(0).getHeight());

		p = P("<3X5:b.png>");
		assertTrue(p.ele(0).isImage());
		assertEquals("b.png", p.ele(0).getSrc().getValue());
		assertEquals(3, p.ele(0).getWidth());
		assertEquals(5, p.ele(0).getHeight());

		p = P("<x5:b.png>");
		assertTrue(p.ele(0).isImage());
		assertEquals("b.png", p.ele(0).getSrc().getValue());
		assertEquals(0, p.ele(0).getWidth());
		assertEquals(5, p.ele(0).getHeight());

		p = P("<3x:b.png>");
		assertTrue(p.ele(0).isImage());
		assertEquals("b.png", p.ele(0).getSrc().getValue());
		assertEquals(3, p.ele(0).getWidth());
		assertEquals(0, p.ele(0).getHeight());
	}

	@Test
	public void test_image_remote() {
		ZBlock p = P("<http://a.com/a.gif>");
		ZEle img = p.ele(0);
		assertEquals("http://a.com/a.gif", img.getSrc().getPath());
		assertTrue(img.getSrc().isHttp());
	}

	@Test
	public void teset_image_in_link_remote() {
		ZBlock p = P("[http://abc.com <http://a.com/a.gif>]");
		ZEle img = p.ele(0);
		assertEquals("http://abc.com", img.getHref().getPath());
		assertEquals("http://a.com/a.gif", img.getSrc().getPath());
		assertTrue(img.getSrc().isHttp());
	}

	@Test
	public void test_simple_link() {
		ZBlock p = P("[a.html]");
		assertEquals("a.html", p.ele(0).getText());
		assertEquals("a.html", p.ele(0).getHref().getValue());
	}

	@Test
	public void test_text_link() {
		ZBlock p = P("[a.html A B]");
		assertEquals("A B", p.ele(0).getText());
		assertEquals("a.html", p.ele(0).getHref().getValue());
	}

	@Test
	public void test_link_in_style() {
		ZBlock p = P("{*/[A]}B");
		assertEquals("A", p.ele(0).getText());
		assertEquals("A", p.ele(0).getHref().getValue());
		assertTrue(p.ele(0).getStyle().getFont().isBold());
		assertTrue(p.ele(0).getStyle().getFont().isItalic());

		assertEquals("B", p.ele(1).getText());
	}

	@Test
	public void test_link_refer_path_and_value() {
		ZRefer href = P("[A]").ele(0).getHref();
		assertEquals("A", href.getPath());
		assertEquals("A", href.getValue());
		assertEquals("A", href.toString());

		href = P("[$A]").ele(0).getHref();
		assertEquals("$A", href.getPath());
		assertEquals("A", href.getValue());
		assertEquals("$A", href.toString());

		href = P("[#A]").ele(0).getHref();
		assertEquals("#A", href.getPath());
		assertEquals("A", href.getInner());
		assertEquals("#A", href.toString());

		href = P("[file:///A]").ele(0).getHref();
		assertEquals("file:///A", href.getPath());
		assertEquals("A", href.getValue());
		assertEquals("file:///A", href.toString());
	}

	@Test
	public void test_style_in_link() {
		ZBlock p = P("[b.html {*/A}]B");
		assertEquals("b.html", p.ele(0).getHref().getValue());
		assertEquals("A", p.ele(0).getText());
		assertTrue(p.ele(0).getStyle().getFont().isBold());
		assertTrue(p.ele(0).getStyle().getFont().isItalic());

		assertEquals("B", p.ele(1).getText());
	}

	@Test
	public void test_style_in_link_partly() {
		ZBlock p = P("[b.html {*/A}T]B");
		assertEquals("b.html", p.ele(0).getHref().getValue());
		assertEquals("AT", p.ele(0).getText());
		assertTrue(p.ele(0).getStyle().getFont().isBold());
		assertTrue(p.ele(0).getStyle().getFont().isItalic());

		assertEquals("B", p.ele(1).getText());
	}

	@Test
	public void test_link_in_style_partly() {
		ZBlock p = P("{*/A[b.html T]}B");
		assertEquals("b.html", p.ele(0).getHref().getValue());
		assertEquals("AT", p.ele(0).getText());
		assertTrue(p.ele(0).getStyle().getFont().isBold());
		assertTrue(p.ele(0).getStyle().getFont().isItalic());

		assertEquals("B", p.ele(1).getText());
	}

	@Test
	public void test_nest_var() {
		ZBlock p = P("A{*`${X}`}B");
		assertEquals("A", p.ele(0).getText());
		assertEquals("${X}", p.ele(1).getText());
		assertTrue(p.ele(1).getStyle().font().isBold());
		assertEquals("B", p.ele(2).getText());
	}
}
