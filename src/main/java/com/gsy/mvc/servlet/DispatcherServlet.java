package com.gsy.mvc.servlet;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gsy.mvc.annotation.Autowired;
import com.gsy.mvc.annotation.Controller;
import com.gsy.mvc.annotation.RequestMapping;
import com.gsy.mvc.annotation.Service;
import com.gsy.mvc.controller.StuController;

/**
 * @author Grasenyu
 * 2019-12-09 19:36
 */
public class DispatcherServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	
	List<String> classNames = new ArrayList<String>();
	//定义一个简化的ioc容器
	Map<String,Object> beans = new HashMap<String, Object>(); 
	
	Map<String,Object> handlerMap = new HashMap<String, Object>();
	
	/**
	 * 初始化容器 扫描 实例化bean UrlMapping 
	 */
	public void init(ServletConfig config) throws ServletException {
		//扫描
		scanPackage("com.gsy");
		//实例化
		doInstance();
		//注入
		doAutowired();
		//请求路径
		urlMapping();
	}


	/**
	 * http://127.0.0.1:8080/stu/getstuall	--->method
	 */
	private void urlMapping() {
		for (Map.Entry<String, Object> entry : beans.entrySet()) {
			Object instance = entry.getValue();
			Class<?> clazz = instance.getClass();
			if (clazz.isAnnotationPresent(Controller.class)) {
				RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
				String classPath = requestMapping.value();  //拿到@RequestMapping  上面的路径
				
				Method[] methods = clazz.getMethods();
				for (Method method : methods) {
					//判断哪个方法上面使用了@RequestMapping
					if (method.isAnnotationPresent(RequestMapping.class)) {
						RequestMapping mrqm = method.getAnnotation(RequestMapping.class);
						String methodPath = mrqm.value();
						handlerMap.put(classPath+methodPath, method);
					}else {
						continue;
					}
				}
			}else {
				continue;
			}
		}
	}
	private void doAutowired() {
		for (Map.Entry<String, Object> entry : beans.entrySet()) {
			//先吧beans里面的实例化对象拿出来
			Object instance = entry.getValue();
			Class<?> clazz = instance.getClass();
			//判断当前类是否使用@Controller注解
			if(clazz.isAnnotationPresent(Controller.class)) {
				Field[] fields = clazz.getDeclaredFields();
				for (Field field : fields) {
					//判断成员变量上面是否有@AutoWired注解
					if (field.isAnnotationPresent(Autowired.class)) {
						System.out.println(field);
						Autowired autowired = field.getAnnotation(Autowired.class);
						String key = autowired.value();
						Object bean = beans.get(key);
						field.setAccessible(true);
						try {
							field.set(instance,bean);
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}else {
						continue;
					}
				}
			}
		}
	}


	private void doInstance() {
		for (String className : classNames) {
			//首先 先把.class去掉
			String com = className.replace(".class","");
			try {
				Class<?> clazz = Class.forName(com);
				if (clazz.isAnnotationPresent(Controller.class)) {
					//进入到这里就是说明这个类上面是有用到@Controller这个注解的
					Object instance = clazz.newInstance();

					if (clazz.getAnnotation(RequestMapping.class) != null) { 
						RequestMapping requestMapping = clazz.getAnnotation(RequestMapping.class);
						beans.put(requestMapping.value(),instance);
					}
				}else if (clazz.isAnnotationPresent(Service.class)) {
					//进入到这里就是说明这个类上面是有用到@Controller这个注解的
					Object instance = clazz.newInstance();
					Service service = clazz.getAnnotation(Service.class);
					String key = service.value();
					beans.put(key,instance);
				}else {
					continue;
				}
				
			} catch (ClassNotFoundException e) {

				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	/**
	 * @param string
	 */
	private void scanPackage(String scanPackage) {
		//扫描路径
		URL url = this.getClass().getClassLoader().getResource("/"+scanPackage.replaceAll("\\.","/"));
		String fileStr = url.getFile();
		System.out.println("-----"+fileStr);
		File file = new File(fileStr);
		String[] filesStr = file.list();
		for (String path : filesStr) {
			File filePath = new File(fileStr+path);
			if (filePath.isDirectory()) {
				scanPackage(scanPackage+"."+path);
				System.out.println(path);
			}else {
				System.out.println(scanPackage);
				System.out.println(filePath.getName());
				classNames.add(scanPackage+"."+filePath.getName());		//这里可以拿到com.gsy....class
			}
		}
	}

	/**
	 * 业务路径请求相关的代码
	 */
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//拿到请求路径	
		String url = req.getRequestURI();
		String contextPath = req.getContextPath();
		String path = url.replace(contextPath, "");
		Method method = (Method) handlerMap.get(path);
		System.out.println(method);
		StuController controller = (StuController) beans.get("/"+path.split("/")[1]);
		Object[] args = hand(req, resp, method);
		try {
			method.invoke(controller, args);
		
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static Object[] hand(HttpServletRequest request,HttpServletResponse response,Method method) {
		//拿到当前执行的方法有哪些参数
		Class<?>[] paramClazzs = method.getParameterTypes();
		System.out.println(paramClazzs);
		// 更新参数的个数，new 一个参数的数组，将方法里面的所有的参数都赋值到args来
		Object args[] = new Object[paramClazzs.length]; 
		int args_i = 0;
		for (Class<?> paramClass : paramClazzs) {
			if (ServletRequest.class.isAssignableFrom(paramClass)) {
				args[args_i++] = request;	
			}
			if (ServletResponse.class.isAssignableFrom(paramClass)) {
				args[args_i++] = response;
			}
		}
		return args;
	}

	
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doGet(req, resp);
	}
	
}
