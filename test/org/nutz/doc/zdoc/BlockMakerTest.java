package org.nutz.doc.zdoc;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.doc.meta.ZBlock;
import org.nutz.doc.meta.ZEle;
import org.nutz.doc.meta.ZRefer;

public class BlockMakerTest {

	private static ZBlock p(String s) {
		return new BlockMaker(s.toCharArray()).make();
	}

	@Test
	public void very_simple_case() {
		ZBlock p = p("A{*B}C");
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
		ZBlock p = p("<b.png>");
		assertTrue(p.ele(0).isImage());
		assertEquals("b.png", p.ele(0).getSrc().getValue());

	}

	@Test
	public void test_image_sizing() {
		ZBlock p = p("<3x5:b.png>");
		assertTrue(p.ele(0).isImage());
		assertEquals("b.png", p.ele(0).getSrc().getValue());
		assertEquals(3, p.ele(0).getWidth());
		assertEquals(5, p.ele(0).getHeight());

		p = p("<3X5:b.png>");
		assertTrue(p.ele(0).isImage());
		assertEquals("b.png", p.ele(0).getSrc().getValue());
		assertEquals(3, p.ele(0).getWidth());
		assertEquals(5, p.ele(0).getHeight());

		p = p("<x5:b.png>");
		assertTrue(p.ele(0).isImage());
		assertEquals("b.png", p.ele(0).getSrc().getValue());
		assertEquals(0, p.ele(0).getWidth());
		assertEquals(5, p.ele(0).getHeight());

		p = p("<3x:b.png>");
		assertTrue(p.ele(0).isImage());
		assertEquals("b.png", p.ele(0).getSrc().getValue());
		assertEquals(3, p.ele(0).getWidth());
		assertEquals(0, p.ele(0).getHeight());
	}

	@Test
	public void test_image_remote() {
		ZBlock p = p("<http://a.com/a.gif>");
		ZEle img = p.ele(0);
		assertEquals("http://a.com/a.gif", img.getSrc().getPath());
		assertTrue(img.getSrc().isHttp());
	}

	@Test
	public void teset_image_in_link_remote() {
		ZBlock p = p("[http://abc.com <http://a.com/a.gif>]");
		ZEle img = p.ele(0);
		assertEquals("http://abc.com", img.getHref().getPath());
		assertEquals("http://a.com/a.gif", img.getSrc().getPath());
		assertTrue(img.getSrc().isHttp());
	}

	@Test
	public void test_simple_link() {
		ZBlock p = p("[a.html]");
		assertEquals("a.html", p.ele(0).getText());
		assertEquals("a.html", p.ele(0).getHref().getValue());
	}

	@Test
	public void test_text_link() {
		ZBlock p = p("[a.html A B]");
		assertEquals("A B", p.ele(0).getText());
		assertEquals("a.html", p.ele(0).getHref().getValue());
	}

	@Test
	public void test_link_in_style() {
		ZBlock p = p("{*/[A]}B");
		assertEquals("A", p.ele(0).getText());
		assertEquals("A", p.ele(0).getHref().getValue());
		assertTrue(p.ele(0).getStyle().getFont().isBold());
		assertTrue(p.ele(0).getStyle().getFont().isItalic());

		assertEquals("B", p.ele(1).getText());
	}

	@Test
	public void test_link_refer_path_and_value() {
		ZRefer href = p("[A]").ele(0).getHref();
		assertEquals("A", href.getPath());
		assertEquals("A", href.getValue());
		assertEquals("A", href.toString());

		href = p("[$A]").ele(0).getHref();
		assertEquals("$A", href.getPath());
		assertEquals("A", href.getValue());
		assertEquals("$A", href.toString());

		href = p("[#A]").ele(0).getHref();
		assertEquals("#A", href.getPath());
		assertEquals("A", href.getInner());
		assertEquals("#A", href.toString());

		href = p("[file:///A]").ele(0).getHref();
		assertEquals("file:///A", href.getPath());
		assertEquals("A", href.getValue());
		assertEquals("file:///A", href.toString());
	}

	@Test
	public void test_style_in_link() {
		ZBlock p = p("[b.html {*/A}]B");
		assertEquals("b.html", p.ele(0).getHref().getValue());
		assertEquals("A", p.ele(0).getText());
		assertTrue(p.ele(0).getStyle().getFont().isBold());
		assertTrue(p.ele(0).getStyle().getFont().isItalic());

		assertEquals("B", p.ele(1).getText());
	}

	@Test
	public void test_style_in_link_partly() {
		ZBlock p = p("[b.html {*/A}T]B");
		assertEquals("b.html", p.ele(0).getHref().getValue());
		assertEquals("AT", p.ele(0).getText());
		assertTrue(p.ele(0).getStyle().getFont().isBold());
		assertTrue(p.ele(0).getStyle().getFont().isItalic());

		assertEquals("B", p.ele(1).getText());
	}

	@Test
	public void test_link_in_style_partly() {
		ZBlock p = p("{*/A[b.html T]}B");
		assertEquals("b.html", p.ele(0).getHref().getValue());
		assertEquals("AT", p.ele(0).getText());
		assertTrue(p.ele(0).getStyle().getFont().isBold());
		assertTrue(p.ele(0).getStyle().getFont().isItalic());

		assertEquals("B", p.ele(1).getText());
	}

	@Test
	public void test_nest_var() {
		ZBlock p = p("A{*`${X}`}B");
		assertEquals("A", p.ele(0).getText());
		assertEquals("${X}", p.ele(1).getText());
		assertTrue(p.ele(1).getStyle().font().isBold());
		assertEquals("B", p.ele(2).getText());
	}
}
