package org.nutz.doc.pdf;

import java.awt.Color;

import org.nutz.doc.ZDocException;
import org.nutz.lang.Lang;

import com.lowagie.text.Anchor;
import com.lowagie.text.Cell;
import com.lowagie.text.Chapter;
import com.lowagie.text.Chunk;
import com.lowagie.text.Font;
import com.lowagie.text.List;
import com.lowagie.text.ListItem;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Section;
import com.lowagie.text.Table;

public class PdfHelper {

	private PdfFonts fonts;

	public PdfHelper() throws ZDocException {
		try {
			fonts = new PdfFonts();
		}
		catch (Exception e) {
			throw Lang.wrapThrow(e, ZDocException.class);
		}
	}

	public Section createSection(int num, String title, int level) {
		return new Chapter(new Paragraph(title, fonts.getHeadingFont(level)), num);
	}

	public Section addSection(Section section, String title, int level) {
		return section.addSection(new Paragraph(title, fonts.getHeadingFont(level)));
	}

	public ListItem createLi() {
		return new ListItem();
	}

	public List createUL() {
		List ul = new List(false, 20);
		ul.setIndentationLeft(20);
		return ul;
	}

	public List createOL() {
		List ul = new List(true, 20);
		ul.setIndentationLeft(20);
		return ul;
	}

	public Paragraph createP() {
		return new Paragraph();
	}

	public Paragraph createNormal() {
		Paragraph p = new Paragraph();
		p.setIndentationLeft(26);
		return p;
	}

	public Table createCodeTable() {
		try {
			Table table = new Table(1);
			table.setPadding(10);
			table.setBorderColor(new Color(200, 200, 200));
			table.setBorderWidth(0);
			table.setBorderWidthLeft(3);
			return table;
		}
		catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
	}

	public Cell createCodeCell(String title) {
		Cell cell = new Cell();
		cell.setBorder(0);
		cell.setBackgroundColor(new Color(240, 240, 240));
		return cell;
	}

	public Table createTable(int columnCount) {
		try {
			Table table = new Table(columnCount);
			table.setBorder(3);
			table.setPadding(6);
			table.setBorderColor(new Color(200, 200, 200));
			return table;
		}
		catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
	}

	public Cell createCell(Paragraph p) {
		Cell cell = new Cell();
		cell.setBorder(1);
		cell.setBorderColor(new Color(200, 200, 200));
		return cell;
	}

	public Chunk createChunk(String text, Font font) {
		return new Chunk(text, font);
	}

	public Anchor createAnchor(String text, String href) {
		Anchor anchor = new Anchor(text, fonts.getAnchorFount());
		anchor.setReference(href);
		return anchor;
	}

	public Anchor createAnchor(String name) {
		Anchor anchor = new Anchor();
		anchor.setName(name);
		return anchor;
	}

	public Font createFont() {
		return new Font(fonts.getNormalFont());
	}

}
