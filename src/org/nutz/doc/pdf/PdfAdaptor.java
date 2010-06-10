package org.nutz.doc.pdf;

import org.nutz.doc.ConvertAdaptor;
import org.nutz.doc.ConvertContext;
import org.nutz.doc.RenderLogger;
import org.nutz.doc.ZDocException;
import org.nutz.doc.zdoc.ZDocSetParser;

public class PdfAdaptor implements ConvertAdaptor {

	@Override
	public void adapt(ConvertContext context) throws ZDocException {
		int maxW = 500;
		int maxH = 500;
		if (context.getArgCount() > 0)
			try {
				maxW = Integer.parseInt(context.getArg(0));
			}
			catch (NumberFormatException e) {}
		if (context.getArgCount() > 1)
			try {
				maxH = Integer.parseInt(context.getArg(1));
			}
			catch (NumberFormatException e) {}

		context.setParser(new ZDocSetParser(context.getIndexml()));
		context.setRender(new PdfDocSetRender(maxW, maxH, new RenderLogger()));
	}

}
