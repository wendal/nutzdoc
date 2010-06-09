package org.nutz.doc;

import java.util.HashMap;
import java.util.Map;

import org.nutz.doc.googlewiki.GoogleWikiAdaptor;
import org.nutz.doc.html.HtmlAdaptor;
import org.nutz.doc.meta.ZDocSet;
import org.nutz.doc.pdf.PdfAdaptor;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

public class Doc {

	private static final Log LOG = Logs.getLog(Doc.class);

	private static Map<String, ConvertAdaptor> map;

	static {
		map = new HashMap<String, ConvertAdaptor>();
		map.put("html", new HtmlAdaptor());
		map.put("gwiki", new GoogleWikiAdaptor());
		map.put("pdf", new PdfAdaptor());
	}

	public static void main(String[] args) throws Exception {
		if (args == null || (args.length == 1 && "help".equals(args[0]))) {
			showHelp();
			return;
		}

		// 参数不足
		if (args.length < 3) {
			LOG.warnf("Wrong parameters!!!");
			showHelp();
			return;
		}

		// 解析参数
		ConvertContext cc = new ConvertContext();
		cc.setSrc(args[1]);
		cc.setDest(args[2]);
		if (args.length > 3) {
			// 看看第三个参数是不是 :xxx.xml
			int i = 3;
			String s = args[3];
			if (s.startsWith(":")) {
				cc.setIndexml(args[3].substring(1));
				i++;
			} else {
				cc.setIndexml("index.xml");
			}
			// 增加自定义参数
			for (; i < args.length; i++)
				cc.addArg(args[i]);
		}

		// 获取 Adaptor
		ConvertAdaptor adaptor = map.get(args[0]);
		if (null == adaptor) {
			LOG.warnf("Unknown type '%s' ", args[0]);
			showHelp();
			return;
		}

		// 执行转换
		adaptor.adapt(cc);
		ZDocSet set = cc.getParser().parse(cc.getSrc());
		cc.getRender().render(cc.getDest(), set);

		LOG.info("~ DONE FOR ALL!");
	}

	private static void showHelp() {
		LOG.warnf(Strings.dup('-', 80));
		LOG.info(Lang.readAll(Streams.fileInr("org/nutz/doc/hlp.man")));
		LOG.warnf(Strings.dup('-', 80));
	}

}
