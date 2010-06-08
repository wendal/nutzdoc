package org.nutz.doc;

import java.io.IOException;

import org.nutz.doc.meta.ZDocSet;

/**
 * 解析一个文档集合，不同的解析器对于 srcPath 参数可以有不同的理解
 * <p>
 * 比如一个最朴素的理解 就是一个目录的路径
 * <p>
 * 无论如何，它需要返回一个 ZDocSet 结构
 * 
 * @author zozoh(zozohtnt@gmail.com)
 * 
 * @see org.nutz.doc.meta.ZDocSet
 */
public interface FolderParser {

	ZDocSet parse(String src) throws IOException;

}
