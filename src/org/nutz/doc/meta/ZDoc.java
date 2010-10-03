package org.nutz.doc.meta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.nutz.lang.Files;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Disks;
import org.nutz.lang.util.NutMap;

/**
 * 一个 ZDoc 文档的数据结构
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public class ZDoc extends ZItem {

	private static final String META_TITLE = "title";

	private static final String META_VERIFIER = "verifier";

	private static final String META_AUTHOR = "author";

	public ZDoc() {
		super();
		root = new ZBlock().setDoc(this);
		attrs = new NutMap();
		metas = new HashMap<String, List<String>>();
	}

	/**
	 * 这个文档的来源，即从何处解析
	 * <p>
	 * 比如如果从一个文件解析，它就是这个文件的全路径
	 */
	private String source;

	/**
	 * 文档的根节点
	 */
	private ZBlock root;

	/**
	 * 文档的元数据，比如 #xxx:xxx
	 * <p>
	 * 同名的 meta 将组成一个字符串列表
	 */
	private Map<String, List<String>> metas;

	/**
	 * 文档的扩展属性，提供给不同的解析器以及渲染器使用
	 */
	private NutMap attrs;

	public Object getAttr(String name) {
		return attrs.get(name);
	}

	public boolean hasAttr(String name) {
		return attrs.containsKey(name);
	}

	public ZDoc removeAttr(String name) {
		attrs.remove(name);
		return this;
	}

	public ZDoc clearAttrs() {
		attrs.clear();
		return this;
	}

	public ZDoc setAttr(String name, Object value) {
		attrs.put(name, value);
		return this;
	}

	/**
	 * 根据名称获取一个 meta，如果 meta 有重名，只返回第一个值
	 * 
	 * @param name
	 *            meta 的名称
	 * @return 值
	 */
	public String getMeta(String name) {
		List<String> list = metas.get(name);
		if (null == list || list.isEmpty())
			return null;
		return list.get(list.size() - 1);
	}

	public List<String> getMetaList(String name) {
		return metas.get(name);
	}

	public boolean hasMeta(String name) {
		List<String> list = metas.get(name);
		return null != list && list.size() > 0;
	}

	public ZDoc removeMeta(String name) {
		metas.remove(name);
		return this;
	}

	public ZDoc clearMetas() {
		metas.clear();
		return this;
	}

	public ZDoc addMeta(String name, String value) {
		List<String> list = metas.get(name);
		if (null == list) {
			list = new ArrayList<String>(5);
			metas.put(name, list);
		}
		list.add(Strings.trim(value));
		return this;
	}

	public ZDoc setMeta(String name, String value) {
		List<String> list = new ArrayList<String>(1);
		list.add(Strings.trim(value));
		metas.put(name, list);
		return this;
	}

	public String getTitle() {
		String title = getMeta(META_TITLE);
		return Strings.isBlank(title) ? null == source ? title : Files.getMajorName(source) : title;
	}

	public ZDoc setTitle(String title) {
		setMeta(META_TITLE, title);
		return this;
	}

	public ZItem addAuthor(String author) {
		return addMeta(META_AUTHOR, author);
	}

	public ZItem addVerifier(String verifier) {
		return addMeta(META_VERIFIER, verifier);
	}

	public Author[] authors() {
		return toAuthorArray(META_AUTHOR);
	}

	public boolean hasAuthor() {
		return hasMeta(META_AUTHOR);
	}

	public boolean hasVerifier() {
		return hasMeta(META_VERIFIER);
	}

	public Author[] verifiers() {
		return toAuthorArray(META_VERIFIER);
	}

	private Author[] toAuthorArray(String name) {
		List<String> list = metas.get(name);
		if (list == null)
			return new Author[0];
		Author[] re = new Author[list.size()];
		int i = 0;
		for (String s : list)
			re[i++] = ZD.author(s);
		return re;
	}

	public String getSource() {
		return source;
	}

	public ZDoc setSource(String source) {
		this.source = source;
		return this;
	}

	public ZBlock root() {
		return root;
	}

	public String getRelativePath(String filePath) {
		return Disks.getRelativePath(source, filePath);
	}

}
