package org.nutz.doc.pdf;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

import org.nutz.lang.Files;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.BaseFont;

/**
 * 一个PDF 所有的字体：
 * <ul>
 * <li>标题 - 级别从 1 至 无限
 * <li>正文 - 各处文字
 * <li>链接 - 超链接
 * </ul>
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class PdfFonts {

	private BaseFont baseFont;

	/**
	 * 第一个（0）为默认标题
	 */
	private Font[] headings;

	private Font anchorFount;

	private Font normalFont;

	private Font codeTypeFont;

	private Font codeFont;

	public PdfFonts() throws DocumentException, IOException {
		File fontFile = Files.findFile("pdf_font.ttf");
		if (null != fontFile)
			baseFont = BaseFont.createFont(	"pdf_font.ttf",
											BaseFont.IDENTITY_H,
											BaseFont.NOT_EMBEDDED);
		else if (null != (fontFile = Files.findFile("pdf_font.ttc")))
			baseFont = BaseFont.createFont(	fontFile.getAbsolutePath() + ",1",
											BaseFont.IDENTITY_H,
											BaseFont.NOT_EMBEDDED);

		/*
		 * Normal
		 */
		normalFont = getFont(12);
		/*
		 * Heading
		 */
		headings = new Font[4];
		headings[0] = bold(getFont(12));
		headings[1] = bold(getFont(18));
		headings[2] = bold(getFont(16));
		headings[3] = bold(getFont(14));

		/*
		 * Anchor
		 */
		anchorFount = getFont(12);
		anchorFount.setColor(new Color(0, 0, 255));
		anchorFount.setStyle(Font.UNDERLINE);

		/*
		 * Code title
		 */
		codeTypeFont = getFont(12);
		codeTypeFont.setColor(new Color(150, 150, 150));
		codeTypeFont.setStyle(Font.ITALIC | Font.UNDERLINE);

		/*
		 * Code base
		 */
		codeFont = getFont(12);
		codeFont.setColor(new Color(150, 0, 0));

	}

	public static Font bold(Font font) {
		font.setStyle(Font.BOLD);
		return font;
	}

	public Font getFont(float size) {
		if (null != baseFont)
			return new Font(baseFont, size, Font.NORMAL);
		Font re = new Font();
		re.setSize(size);
		return re;
	}

	public Font getHeadingFont(int level) {
		if (level < headings.length)
			return headings[level];
		return headings[0];
	}

	public Font getAnchorFount() {
		return anchorFount;
	}

	public Font getNormalFont() {
		return normalFont;
	}

	public Font getCodeTypeFont() {
		return codeTypeFont;
	}

	public Font getCodeFont() {
		return codeFont;
	}
}
