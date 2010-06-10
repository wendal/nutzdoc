package org.nutz.doc.pdf;

import java.awt.Color;

import org.nutz.doc.ZDocException;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

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

	public Section section(int num, String title, int level) {
		Paragraph p = new Paragraph(title, fonts.getHeadingFont(level));
		p.setSpacingBefore(10);
		p.setSpacingAfter(10);
		return new Chapter(p, num);
	}

	public Section addSection(Section section, String title, int level) {
		return section.addSection(new Paragraph(title, fonts.getHeadingFont(level)));
	}

	public ListItem LI() {
		return new ListItem();
	}

	public List UL() {
		List ul = new List(false, 20);
		ul.setIndentationLeft(20);
		return ul;
	}

	public List OL() {
		List ul = new List(true, 20);
		ul.setIndentationLeft(20);
		return ul;
	}

	public Paragraph p() {
		return new Paragraph();
	}

	public Paragraph blank() {
		Paragraph p = p();
		p.add(new Chunk(" ", fonts.getNormalFont()));
		return p;
	}

	public Paragraph normal() {
		Paragraph p = new Paragraph();
		p.setIndentationLeft(16);
		p.setSpacingBefore(8);
		p.setSpacingAfter(8);
		return p;
	}

	public Table codeTable() {
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

	public Cell codeCell(String title) {
		Cell cell = new Cell();
		cell.setBorder(0);
		cell.setBackgroundColor(new Color(240, 240, 240));
		Paragraph t = this.p();
		t.add(chunk("[" + (Strings.isBlank(title) ? "UNKNOWN" : title) + "]",
					fonts.getCodeTypeFont()));
		cell.add(t);
		cell.add(blank());
		return cell;
	}

	public Table table(int columnCount) {
		try {
			Table table = new Table(columnCount);
			table.setBorder(0);
			table.setAlignment(Table.ALIGN_LEFT);
			table.setPadding(4);
			table.setBorderColor(new Color(200, 200, 200));
			return table;
		}
		catch (Exception e) {
			throw Lang.wrapThrow(e);
		}
	}

	public Cell cell() {
		Cell cell = new Cell();
		cell.setBorderWidth(1);
		cell.setBorderColor(new Color(200, 200, 200));
		cell.setHorizontalAlignment(Cell.ALIGN_LEFT);
		cell.setVerticalAlignment(Cell.ALIGN_TOP);
		return cell;
	}

	public Chunk chunk(String text, Font font) {
		return new Chunk(text, font);
	}

	public Paragraph codeLine(String text) {
		Paragraph p = p();
		p.add(new Chunk(text, fonts.getCodeFont()));
		return p;
	}

	public Anchor anchor(String text, String href) {
		Anchor anchor = new Anchor(text, fonts.getAnchorFount());
		anchor.setReference(href);
		return anchor;
	}

	public Anchor anchor(String name) {
		Anchor anchor = new Anchor();
		anchor.setName(name);
		return anchor;
	}

	public Font font() {
		return new Font(fonts.getNormalFont());
	}

}
