package com.morgan.library.async;

/**
 * 一个可销毁接口
 * 
 * @author Morgan.Ji
 */
public interface IDestroy {

	/**
	 * 异步线程是否可以cancel掉，例如有的后台处理不能终端。
	 */
	public boolean cancelable();

	/**
	 * 销毁对象，释放资源
	 */
	public void destory();
}
