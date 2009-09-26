package org.nutz.doc.zdoc;

import static org.junit.Assert.*;

import org.junit.Test;
import org.nutz.doc.meta.ZBlock;
import org.nutz.doc.meta.ZEle;

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
		assertEquals("b.png", p.ele(0).getSrc().getPath());

	}

	@Test
	public void test_image_sizing() {
		ZBlock p = p("<3x5:b.png>");
		assertTrue(p.ele(0).isImage());
		assertEquals("b.png", p.ele(0).getSrc().getPath());
		assertEquals(3, p.ele(0).getWidth());
		assertEquals(5, p.ele(0).getHeight());

		p = p("<3X5:b.png>");
		assertTrue(p.ele(0).isImage());
		assertEquals("b.png", p.ele(0).getSrc().getPath());
		assertEquals(3, p.ele(0).getWidth());
		assertEquals(5, p.ele(0).getHeight());

		p = p("<x5:b.png>");
		assertTrue(p.ele(0).isImage());
		assertEquals("b.png", p.ele(0).getSrc().getPath());
		assertEquals(0, p.ele(0).getWidth());
		assertEquals(5, p.ele(0).getHeight());

		p = p("<3x:b.png>");
		assertTrue(p.ele(0).isImage());
		assertEquals("b.png", p.ele(0).getSrc().getPath());
		assertEquals(3, p.ele(0).getWidth());
		assertEquals(0, p.ele(0).getHeight());
	}

	@Test
	public void test_simple_link() {
		ZBlock p = p("[a.html]");
		assertEquals("a.html", p.ele(0).getText());
		assertEquals("a.html", p.ele(0).getHref().getPath());
	}

	@Test
	public void test_text_link() {
		ZBlock p = p("[a.html A B]");
		assertEquals("A B", p.ele(0).getText());
		assertEquals("a.html", p.ele(0).getHref().getPath());
	}

	@Test
	public void test_link_in_style() {
		ZBlock p = p("{*/[A]}B");
		assertEquals("A", p.ele(0).getText());
		assertEquals("A", p.ele(0).getHref().getPath());
		assertTrue(p.ele(0).getStyle().getFont().isBold());
		assertTrue(p.ele(0).getStyle().getFont().isItalic());

		assertEquals("B", p.ele(1).getText());
	}

	@Test
	public void test_style_in_link(){
		ZBlock p = p("[b.html {*/A}]B");
		assertEquals("b.html", p.ele(0).getHref().getPath());
		assertEquals("A", p.ele(0).getText());
		assertTrue(p.ele(0).getStyle().getFont().isBold());
		assertTrue(p.ele(0).getStyle().getFont().isItalic());

		assertEquals("B", p.ele(1).getText());
	}
	
	@Test
	public void test_style_in_link_partly(){
		ZBlock p = p("[b.html {*/A}T]B");
		assertEquals("b.html", p.ele(0).getHref().getPath());
		assertEquals("AT", p.ele(0).getText());
		assertTrue(p.ele(0).getStyle().getFont().isBold());
		assertTrue(p.ele(0).getStyle().getFont().isItalic());

		assertEquals("B", p.ele(1).getText());
	}
	
	@Test
	public void test_link_in_style_partly(){
		ZBlock p = p("{*/A[b.html T]}B");
		assertEquals("b.html", p.ele(0).getHref().getPath());
		assertEquals("AT", p.ele(0).getText());
		assertTrue(p.ele(0).getStyle().getFont().isBold());
		assertTrue(p.ele(0).getStyle().getFont().isItalic());

		assertEquals("B", p.ele(1).getText());
	}
}
