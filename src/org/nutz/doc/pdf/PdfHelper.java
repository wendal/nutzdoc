package org.nutz.doc.pdf;

import java.io.IOException;

import com.lowagie.text.Chapter;
import com.lowagie.text.DocumentException;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Section;

public class PdfHelper {

	private PdfFonts fonts;

	public PdfHelper() throws DocumentException, IOException {
		fonts = new PdfFonts();
	}

	public Section createSection(int num, String title, int level) {
		return new Chapter(new Paragraph(title, fonts.getHeadingFont(level)), num);
	}

	public Section addSection(Section section, String title, int level) {
		return section.addSection(new Paragraph(title, fonts.getHeadingFont(level)));
	}

	public ListItem createLi() {

		return null;
	}

	public List createUL() {
		return null;
	}

	public List createOL() {
		return null;
	}

	public Paragraph createP() {
		return null;
	}

}
