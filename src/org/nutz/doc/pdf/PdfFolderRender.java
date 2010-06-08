package org.nutz.doc.pdf;

import java.io.File;
import java.io.IOException;

import org.nutz.doc.FolderRender;
import org.nutz.doc.meta.ZFolder;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.util.Node;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.pdf.PdfWriter;

/**
 * 它将将把一个 ZDoc 的目录，变成一个 PDF 文件
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class PdfFolderRender implements FolderRender {

	@Override
	public void render(File dest, Node<ZFolder> root) throws IOException {
		if (!dest.exists())
			Files.createNewFile(dest);

		try {
			// 创建 PDF 文档
			Document doc = new Document();
			PdfWriter.getInstance(doc, Streams.fileOut(dest));
			doc.open();

			// 关闭 PDF 文档
			doc.close();
		}
		catch (DocumentException e) {
			throw Lang.wrapThrow(e, IOException.class);
		}

	}

}
