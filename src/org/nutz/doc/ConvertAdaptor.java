package org.nutz.doc;

/**
 * 根据上下文环境，设置 parser 和 render
 * 
 * @author zozoh(zozohtnt@gmail.com)
 */
public interface ConvertAdaptor {

	void adapt(ConvertContext context) throws ZDocException;

}
