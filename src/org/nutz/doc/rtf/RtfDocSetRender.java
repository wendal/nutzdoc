package org.nutz.doc.rtf;

import org.nutz.doc.RenderLogger;
import org.nutz.doc.ZDocException;
import org.nutz.doc.meta.ZBlock;
import org.nutz.doc.meta.ZDoc;
import org.nutz.doc.pdf.PdfDocSetRender;
import org.nutz.lang.Streams;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Section;
import com.lowagie.text.rtf.RtfWriter2;

public class RtfDocSetRender extends PdfDocSetRender {

	public RtfDocSetRender(int maxImgWidth, int maxImgHeight, RenderLogger L)
			throws ZDocException {
		super(maxImgWidth, maxImgHeight, L);
	}

	@Override
	public void prepareDocument(String dest, Document document)
			throws DocumentException {
		RtfWriter2.getInstance(document, Streams.fileOut(dest));
	}
	
	protected void renderToSection(Section section, ZDoc doc, int depth) {
		// 增加自己
		Section docSection = helper.addSection(section, doc.getTitle(), depth, null);
		// 增加自己的字节点
		for (ZBlock block : doc.root().children()) {
			renderBlockToSection(docSection, block, depth);
		}
	}
}
