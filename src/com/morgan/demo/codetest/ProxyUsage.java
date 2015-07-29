package com.morgan.demo.codetest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * 关于代理类的测试
 * 
 * @author Morgan.Ji
 * 
 */
public class ProxyUsage {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		testStaticProxy();
		testJDKProxy();
		testCGLibProxy();
	}

	/**
	 * 静态代理测试
	 */
	private static void testStaticProxy() {
		Request req = new HttpRequest();
		RequestProxy proxy = new RequestProxy();
		proxy.buildProxy(req);
		proxy.request();
	}

	/**
	 * CGLib动态代理测试
	 */
	private static void testCGLibProxy() {
		Student stu = new Student();
		CGLibProxy proxy = new CGLibProxy();
		Student stuProxy = (Student) proxy.createProxy(stu);
		stuProxy.doSomeThing();
	}

	/**
	 * JDK动态代理测试
	 */
	private static void testJDKProxy() {
		Request req = new HttpRequest();
		JDKProxy proxy = new JDKProxy();
		Request reqProxy = (Request) proxy.createProxy(req);
		reqProxy.request();
	}

}

interface Request {
	public void request();
}

class HttpRequest implements Request {

	@Override
	public void request() {
		System.out.println("http request......");
	}
}

/**
 * 委托类(静态代理)
 * 
 * @author Morgan.Ji
 */
class RequestProxy implements Request {
	// 被代理的类
	private Request req;

	public void buildProxy(Request req) {
		this.req = req;
	}

	@Override
	public void request() {
		// 调用之前
		doBefore();
		req.request();
		// 调用之后
		doAfter();
	}

	private void doAfter() {
		System.out.println("after method invoke");
	}

	private void doBefore() {
		System.out.println("before method invoke");
	}
}

/**
 * 动态代理类
 * 
 * @author Morgan.Ji
 * 
 */
class JDKProxy implements InvocationHandler {
	private Object target;

	public Object createProxy(Object target) {
		this.target = target;
		return Proxy.newProxyInstance(target.getClass().getClassLoader(), target
				.getClass().getInterfaces(), this);
	}

	/**
	 * @param proxy
	 *            被代理的类
	 * @param method
	 *            要调用的方法
	 * @param args
	 *            方法调用时所需要的参数
	 *            可以将InvocationHandler接口的子类想象成一个代理的最终操作类，替换掉ProxySubject
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

		Object retVal = null;
		// 调用之前
		doBefore();
		retVal = method.invoke(target, args);
		// 调用之后
		doAfter();
		return retVal;
	}

	private void doAfter() {
		System.out.println("after method invoke");
	}

	private void doBefore() {
		System.out.println("before method invoke");
	}
}

class Student {

	public void doSomeThing() {
		System.out.println("day day study,day day up...");
	}
}

/**
 * 通过CGLib实现动态代理
 * 
 */
class CGLibProxy implements MethodInterceptor {
	// 要代理的原始对象
	private Object obj;
	private Enhancer enhancer = new Enhancer();

	public Object createProxy(Object target) {
		this.obj = target;
		enhancer.setSuperclass(this.obj.getClass());// 设置代理目标
		enhancer.setCallback(this);// 设置回调
		enhancer.setClassLoader(target.getClass().getClassLoader());
		return enhancer.create();
	}

	/**
	 * 在代理实例上处理方法调用并返回结果
	 * 
	 * @param proxy
	 *            代理类
	 * @param method
	 *            被代理的方法
	 * @param params
	 *            该方法的参数数组
	 * @param methodProxy
	 */
	@Override
	public Object intercept(Object proxy, Method method, Object[] params,
			MethodProxy methodProxy) throws Throwable {
		Object result = null;
		// 调用之前
		doBefore();
		// 调用原始对象的方法
		result = methodProxy.invokeSuper(proxy, params);
		// 调用之后
		doAfter();
		return result;
	}

	private void doAfter() {
		System.out.println("after method invoke");
	}

	private void doBefore() {
		System.out.println("before method invoke");
	}

}
