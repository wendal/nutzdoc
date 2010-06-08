package org.nutz.doc.zdoc;

import java.io.File;
import java.io.IOException;

import org.nutz.doc.FolderParser;
import org.nutz.doc.meta.ZDocSet;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;

/**
 * 本解析器，将根据一个目录，以及其内的 "index.xml"[可选] 来进行解析
 * <p>
 * <h2>如果该目录有 index.xml</h2><br>
 * 那么这个文件的格式应该为 <b>doc</b> 的嵌套，<b>doc</b> 可以有的属性为：
 * <ul>
 * <li>path - 必需：对应路径或者文件，如果是目录，则所有子元素的相对路径将改变
 * <li>title - 可选：否则采用 文档标题或者目录名
 * <li>author - 可选： 逗号分隔，表示以下文档的默认作者
 * <li>verifier - 可选： 逗号分隔，表示以下文档的默认检查者
 * <li>skip - 可选：仅仅改变工作目录
 * </ul>
 * 
 * 根节点 <b>doc</b> 对应当前集合的根目录
 * 
 * <h2>不存在 index.xml</h2> <br>
 * 将按照目录结构搜索全部的文档，当然忽略所有的隐藏文件， indexXml 的名字以及 zdoc 的扩展名由构造时指定
 * 
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class ZDocFolderParser implements FolderParser {

	private String indexmlPath;

	public ZDocFolderParser(String indexmlPath) {
		this.indexmlPath = indexmlPath;
	}

	public ZDocSet parse(String src) throws IOException {
		File dir = Files.findFile(src);
		if (null == dir)
			throw Lang.makeThrow("Source '%s' not exists!", src);
		if (!dir.isDirectory())
			throw Lang.makeThrow("Source '%s' must be a directory!", src);

		File indexml = Files.getFile(dir, indexmlPath);
		ZDocSet set = new ZDocSet(dir.getName()).setSrc(src);
		try {
			if (indexml.exists())
				(new IndexXmlSetParing(indexml, dir)).doParse(set);
			else
				(new NoIndexSetParsing(dir, "^(.+[.])(zdoc|man)$")).doParse(set);
		}
		catch (Exception e) {
			throw Lang.wrapThrow(e);
		}

		return set;
	}

}
