package org.nutz.doc.googlewiki;

import org.nutz.doc.ConvertContext;
import org.nutz.doc.RenderLogger;
import org.nutz.doc.ConvertAdaptor;
import org.nutz.doc.ZDocException;
import org.nutz.doc.zdoc.ZDocSetParser;
import org.nutz.lang.Lang;

/**
 * 接受的参数有如下意义
 * <ul>
 * <li>args[0] - [必须] 索引 WIKI 文件名
 * <li>args[1] - [必须] 图片地址 URL
 * </ul>
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class GoogleWikiAdaptor implements ConvertAdaptor {

	@Override
	public void adapt(ConvertContext context) throws ZDocException {
		if (context.getArgCount() < 2)
			throw Lang.makeThrow("Lack indexWikiName, imageAddress");

		context.setParser(new ZDocSetParser(context.getIndexml()));
		context.setRender(new GoogleWikiDocSetRender(	context.getArg(0),
														context.getArg(1),
														new RenderLogger()));
	}
}
