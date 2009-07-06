package org.nutz.doc.pdf;

import java.io.File;
import java.io.OutputStream;

import org.nutz.doc.Doc;
import org.nutz.doc.DocRender;
import org.nutz.doc.Line;
import org.nutz.doc.Block;
import org.nutz.doc.style.FontStyle;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

import com.lowagie.text.Chapter;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Section;
import com.lowagie.text.pdf.PdfWriter;

public class PdfDocRender implements DocRender {

	private static Font chinese = null;

	static {
		try {
			File fontFile = Files.findFile("org/nutz/doc/pdf/font.ttf");
			// BaseFont bfChinese =
			// BaseFont.createFont(fontFile.getAbsolutePath(),
			// BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
			// chinese = new Font(bfChinese);
			// chinese.setFamily("NUTZ-PDF");
			FontFactory.register(fontFile.getAbsolutePath(), "@nutz");
		} catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
	}

	public static void main(String[] args) {
		for (Object f : FontFactory.getRegisteredFonts()) {
			System.out.printf("%40s:%s\n", f.getClass().getSimpleName(), f.toString());
		}
		System.out.println(Strings.dup('-', 50));
		Font font = FontFactory.getFont("@nutz");
		font.setStyle(Font.BOLDITALIC | Font.STRIKETHRU);
		System.out.println(font.getFamilyname());
		System.out.println(font.isBold());
		System.out.println(font.isStrikethru());
		System.out.println(font.isItalic());
	}

	static Paragraph p(String txt) {
		return new Paragraph(txt, chinese);
	}

	static Font font(FontStyle fs) {
		Font font = FontFactory.getFont("@nutz");
		if (fs.isBold())
			font.setStyle(Font.BOLD);
		if (fs.isItalic())
			font.setStyle(Font.ITALIC);
		if (fs.isStrike())
			font.setStyle(Font.STRIKETHRU);
		return font;
	}

	@Override
	public void render(OutputStream ops, Doc doc) {
		new InnerRender(ops, doc).render();
	}

	private static class InnerRender {
		private Document pdf;
		private Doc doc;

		InnerRender(OutputStream ops, Doc doc) {
			this.pdf = new Document(PageSize.A4, 50, 50, 50, 50);
			try {
				PdfWriter.getInstance(pdf, ops);
			} catch (DocumentException e) {
				throw Lang.wrapThrow(e);
			}
			this.doc = doc;
		}

		void render() {
			pdf.open();
			Chapter chap = new Chapter(p(doc.getTitle()), 0);
			Block[] ps = doc.root().getBlocks();
			for (Block p : ps)
				renderBlock(chap, p);
			try {
				pdf.add(chap);
			} catch (DocumentException e) {
				throw Lang.wrapThrow(e);
			}
			pdf.close();
		}

		private void renderBlock(Section section, Block p) {
			if (p.isIndexTable()) {
				// Ignore IndexTable
			} else if (p.isOrderedList()) {

			} else if (p.isUnorderedList()) {

			} else if (p.isCode()) {
				section.add(p(p.line(0).getText()));
			} else if (p.isHeading()) {
				for (Line h : p.lines()) {
					Section sec = section.addSection(h.getText());
					Block[] pps = h.getBlocks();
					for (Block pp : pps)
						renderBlock(sec, pp);
				}
			} else {
				section.add(renderLine(p.lines()));
			}
		}

		private Paragraph renderLine(Line[] l) {
			return null;
		}

	}

}
