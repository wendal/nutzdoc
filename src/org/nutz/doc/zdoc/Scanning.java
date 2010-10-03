package org.nutz.doc.zdoc;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.IntRange;

class Scanning {

	private static final Pattern INDEX_RANGE = Pattern.compile("^([#]index:)(([0-9]+)([,:][0-9]+)?)([ \t]*)$");
	private static final Pattern META = Pattern.compile("^([#])([a-zA-Z0-9_-]+)([:])(.+)$");

	/**
	 * 一个 \t 相当于几个空格
	 */
	private int tabpar;

	/**
	 * 当前行的深度
	 */
	private int depth;

	/**
	 * 当前行去掉深度标识符后剩余的内容，如果为 null，表示为空行
	 */
	private String remain;

	Scanning(int tabpar) {
		this.tabpar = tabpar;
	}

	ScanResult scan(BufferedReader reader) {
		ScanResult sr = new ScanResult();
		Line root = sr.root();
		Line last = sr.root();
		while (null != readLine(reader, -1)) {
			Line line = null;
			// 如果不为空行 ...
			if (!Strings.isBlank(remain)) {
				// 检查是否为 Index
				Matcher m = INDEX_RANGE.matcher(remain);
				if (m.find()) {
					line = new Line(IntRange.make(m.group(2)));
					appendToLast(root, last, line, depth);
					last = line;
					continue;
				}
				// 检查是否为 meta，meta 并不改变 last 等设定
				if (0 == depth) {
					m = META.matcher(remain);
					if (m.find()) {
						String name = m.group(2);
						String value = m.group(4);
						sr.doc().addMeta(name, value);
						continue;
					}
				}
			}
			// 按照普通行去解析，其中也考虑到了空行的情况
			line = new Line(remain);
			/*
			 * When Code {{{<...> Read to }}}, and save all text to line
			 */
			if (line.isCodeStart()) {
				StringBuilder sb = new StringBuilder();
				int maxDepth = depth;
				while (null != (readLine(reader, maxDepth))) {
					if ("}}}".equals(Strings.trim(remain)))
						break;
					sb.append(remain).append('\n');
				}
				line.setText(sb.toString());
				appendToLast(root, last, line, depth);
			}
			/*
			 * Other Type
			 */
			else {
				appendToLast(root, last, line, depth);
			}
			// Store the last
			last = line;
		}
		return sr;

	}

	private void appendToLast(Line root, Line last, Line line, int depth) {
		// 最后一行为不可有子的行
		if (null != last.getParent())
			if (last.isHr() || last.isCodeStart() || last.isIndexRange() || last.isBlank())
				last = last.getParent();
		// 如果当前行是空行，则不用考虑当前行的深度
		// 否则比较深度，寻找匹配的父行退到上一级
		if (!line.isBlank())
			while (depth < last.depth())
				last = last.getParent();
		// 这个行一定是个有效行，将当前行加为其子节点
		last.add(line);
	}

	/**
	 * 读取一行，这里，如果行尾是 '\' 则继续读取
	 * 
	 * @param reader
	 *            文本输入流
	 */
	private String readLine(BufferedReader reader, int maxDepth) {
		try {
			String str = reader.readLine();
			// 流结束了
			if (null == str) {
				remain = null;
				depth = 0;
				return null;
			}
			// 计算深度
			int pos = countDepth(str, maxDepth);
			// 结尾逃逸行，需要继续读取流
			if (Strings.endsWithChar(str, '\\')) {
				maxDepth = depth;
				StringBuilder sb = new StringBuilder(str.substring(pos, str.length() - 1));
				str = reader.readLine();
				// 循环读取结尾逃逸行
				while (null != str && Strings.endsWithChar(str, '\\')) {
					pos = countDepth(str, maxDepth);
					sb.append(Strings.trim(str.substring(pos, str.length() - 1)));
					str = reader.readLine();
				}
				// 结尾逃逸行以一个普通行结束
				if (null != str) {
					pos = countDepth(str, maxDepth);
					sb.append(Strings.trim(str));
				}
				// 计算结果行
				remain = sb.toString();
			}
			// 计算余下的字符串
			else {
				remain = str.substring(pos);
			}
			return remain;
		}
		catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
	}

	/**
	 * 计算当前文件行的深度
	 * 
	 * @param maxDepth
	 *            如果超过这个深度，则退出计算。默认为 -1，表示无限制
	 * 
	 * @return 当前行的前多少个字符是用来表示行深度的
	 */
	private int countDepth(String str, int maxDepth) {
		depth = 0;
		// 用来记录经历了多少空格，这数字不会大于 tabpar，大于会清零并让 d++
		int i = 0;
		int wsnm = 0;
		int len = str.length();
		for (; i < len; i++) {
			// 最大深度有效，当前已经超过最大深度，退出循环
			if (maxDepth >= 0 && depth >= maxDepth)
				break;
			// 开始分析字符
			char c = str.charAt(i);
			// 空格压栈
			if (c == ' ') {
				wsnm++;
				if (wsnm >= tabpar) {
					depth++;
					wsnm = 0;
				}
			}
			// \t 清栈
			else if (c == '\t') {
				depth++;
				wsnm = 0;
			}
			// 其他字符，退出判断
			else {
				break;
			}
		}
		if (wsnm >= tabpar) {
			depth++;
			wsnm = 0;
		}
		return i;
	}
}
