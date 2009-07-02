package org.nutz.doc.pdf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.List;

import org.nutz.doc.Doc;
import org.nutz.doc.DocRender;
import org.nutz.doc.IndexTable;
import org.nutz.doc.Inline;
import org.nutz.doc.Line;
import org.nutz.doc.Media;
import org.nutz.doc.Paragraph;
import org.nutz.doc.Refer;
import org.nutz.doc.style.Font;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

import com.lowagie.text.Chapter;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfWriter;

public class PdfDocRender implements DocRender {

	private static com.lowagie.text.Font chinese = null;
	
	private static com.lowagie.text.Paragraph p(String txt) {
		return new com.lowagie.text.Paragraph(txt, chinese);
	}
	
	
	
	@Override
	public void render(OutputStream ops, Doc doc) {
		if (null == chinese) {
			try {
				File fontFile = Files.findFile("zzh/dom/itext/msyh.ttf");
				BaseFont bfChinese = BaseFont.createFont(fontFile.getAbsolutePath(),
						BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
				chinese = new com.lowagie.text.Font(bfChinese);
			} catch (Exception e) {
				throw Lang.wrapThrow(e);
			}
		}
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
			Paragraph[] ps = doc.root().getParagraphs();
			pdf.close();
		}

		
	}

}
