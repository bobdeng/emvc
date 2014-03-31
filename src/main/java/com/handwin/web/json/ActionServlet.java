package com.handwin.web.json;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.handwin.db.HibernateTool;
import com.handwin.web.UrlCookie;

public class ActionServlet extends HttpServlet {
	public static boolean gzip=false;
	static ActionCreator creator=new DefaultActionCreator();
	
	
	public static void setCreator(ActionCreator creator) {
		ActionServlet.creator = creator;
	}

	static Gson gson = null;
	static {
		gson=new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

	}
	static Map<String, Class<JsonAction>> mapActions = new HashMap<String, Class<JsonAction>>();
	static String[] actionPacks = null;
	public static int DEFAULT_SIZE_MAX = 20 * 1024 * 1024;
	public static int DEFAULT_SIZE_THRESHOLD = 64000;
	private File tmpFilePath = null;
	private static SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
	private static final int hash_json = 3271912;
	private static final int hash_wap1 = 3641867;
	private static final int hash_wap2 = 3641868;
	private static final int hash_webkit = 3796;
	private static final int hash_h4 = 3276;
	private static final int hash_htm=103649;
	private static final int hash_do=3211;
	private Class errorClass = Error.class;

	private static int getRequestType(String path) {
		int index = path.indexOf('.');
		if (index > 0) {
			return path.substring(index + 1).hashCode();
		}
		return 0;
	}

	public static void main(String[] args) throws Exception {
		// System.out.println(getRequestType("/icard/abc.wap1"));
		System.out.println("htm".hashCode());

	}



	@Override
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		//System.out.println(" service(HttpServletRequest request,");
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		boolean forHtml = StringUtils.isBlank(request.getHeader("X-Mobile"));
		forHtml = forHtml
				&& StringUtils.isBlank(request.getParameter("xmobile"));
		response.setContentType(forHtml ? "text/html;charset=utf-8"
				: "application/octet-stream");
		String path = request.getServletPath();
		List items = null;
		int requestType = getRequestType(path);
		try {
			JsonAction.clear();
			JsonAction action = this.getAction(path);
			if (action != null) {
				ActionTools.setClient(request, response);
				Enumeration names = request.getHeaderNames();
				while (names.hasMoreElements()) {
					String name = (String) names.nextElement();
					// System.out.println(name+"="+request.getHeader(name));
					action.setHeader(name, request.getHeader(name));
				}
				action.setRequest(request);
				action.setResponse(response);
				action.setSession(request.getSession());
				action.setReferer(request.getHeader("referer"));
				action.setRemoteAddr(getIpAddr(request));
				action.setRealPath(this.getServletConfig().getServletContext()
						.getRealPath(""));
				//action.parseAttribute(request);
				if (isMultipart) {
					SimpleDateFormat fmt = (SimpleDateFormat) format.clone();
					File tmpFile = new File(tmpFilePath, fmt.format(Calendar
							.getInstance().getTime()));
					if (!tmpFile.exists())
						tmpFile.mkdirs();
					FileItemFactory factory = new DiskFileItemFactory(
							DEFAULT_SIZE_THRESHOLD, tmpFile);

					ServletFileUpload upload = new ServletFileUpload(factory);
					// ������������������
					upload.setSizeMax(DEFAULT_SIZE_MAX);
					// ������������������������������������������������������������������������������������

					try {
						upload.setHeaderEncoding("utf-8");
						items = upload.parseRequest(request);
						Class clz=action.getClass();
						while(!(clz.equals(Object.class)))
						{
							action.parseInput(items,clz.getDeclaredFields());
							action.parseHead(request,clz.getDeclaredFields());
							action.parseCookie(request.getCookies(),clz.getDeclaredFields());
							clz=clz.getSuperclass();
						}
					} catch (Exception e) {
						System.err.println(path+":"+e.getMessage());
						//e.printStackTrace();
						// if (forHtml) {
						// response.getOutputStream().write(
						// gson.toJson(action.onInputError(e.getMessage()))
						// .getBytes("utf-8"));
						doResult(request, response,
								action.onInputError(e.getMessage()),
								requestType, forHtml);
						// }
						return;
					}
				} else
					try {
							
						Class clz=action.getClass();
						while(!(clz.equals(Object.class)))
						{
							action.parseInput(request,clz.getDeclaredFields());
							action.parseHead(request,clz.getDeclaredFields());
							action.parseCookie(request.getCookies(),clz.getDeclaredFields());
							clz=clz.getSuperclass();
						}
					} catch (Exception e) {
						System.err.println(path+":"+e.getMessage());

						e.printStackTrace();
						// response.getOutputStream().write(
						// gson.toJson(action.onInputError(e.getMessage())).getBytes(
						// "utf-8"));
						doResult(request, response,
								action.onInputError(e.getMessage()),
								requestType, forHtml);
						return;

					}
				action.setCookies(request.getCookies());
				boolean hasCookie = (request.getCookies() != null && request
						.getCookies().length > 0);
				action.setUrl(new URL(request.getRequestURL().toString()));
				try {
					List<Cookie> urlCookies = null;
					if (!hasCookie
							|| !StringUtils.isBlank(request
									.getRequestedSessionId())) {
						urlCookies = UrlCookie.getUrlCookies(request);
						if (urlCookies != null && urlCookies.size() > 0) {
							action.setCookies(urlCookies.toArray(new Cookie[0]));
						} 
					}
					if(urlCookies==null) urlCookies=new ArrayList<Cookie>();
					if(action.getClass().isAnnotationPresent(Transaction.class)){
						HibernateTool.beginTransaction();
					}
					Object rlt=null;
					try{
						rlt = action.execute();
						if(action.getClass().isAnnotationPresent(Transaction.class))
							HibernateTool.commitTransaction();
					}catch(RuntimeException e){
						if(action.getClass().isAnnotationPresent(Transaction.class))
						HibernateTool.rollbackTransaction();
						rlt=action.runtimeException(e);
					}finally{
						if(action.getClass().isAnnotationPresent(Transaction.class))
						HibernateTool.closeSession();
					}
					if (rlt instanceof CookieInterface) {
						writeCookie(response, urlCookies, hasCookie,
								((CookieInterface) rlt).getCookies());
					} else {
						if (JsonAction.getWriteCookies() != null) {
							writeCookie(response, urlCookies, hasCookie,
									JsonAction.getWriteCookies());
						}
					}
					if (!hasCookie
							|| !StringUtils.isBlank(request
									.getRequestedSessionId())) {
						request.setAttribute("cookies", urlCookies);
					}
					if (rlt instanceof FileDownload) {
						FileDownload file = (FileDownload) rlt;
						response.setContentType(file.getContentType());
						response.setContentLength(file.getFileData().length);
						response.setHeader("Content-Disposition",
								"attachment; filename=" + file.getFileName());
						response.getOutputStream().write(file.getFileData());
						response.getOutputStream().flush();
					}else if(rlt instanceof ImageShow){
						ImageShow file = (ImageShow) rlt;
						response.setContentType(file.getContentType());
						response.setContentLength(file.getFileData().length);
						response.getOutputStream().write(file.getFileData());
						response.getOutputStream().flush();
					} else {
						doResult(request, response, rlt, requestType, forHtml);

					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					//if (action.needTransaction())
					//	HibernateTool.closeSession();
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (items != null) {
				for (int i = 0; i < items.size(); i++) {
					FileItem item = (FileItem) items.get(i);
					if (!item.isFormField() && !item.isInMemory()) {
						item.delete();
					}
				}
			}
		}

	}

	private void writeCookie(HttpServletResponse response,
			List<Cookie> urlCookies, boolean hasCookie, List<Cookie> cookies) {
		if (cookies != null && cookies.size() > 0) {
			for (Cookie cookie : cookies) {
				response.addCookie(cookie);
				if (!hasCookie) {
					writeCookie(urlCookies, cookie);
				}
			}

		}

	}

	private void doResult(HttpServletRequest request,
			HttpServletResponse response, Object rlt, int requestType,
			boolean forHtml) throws Exception {
		switch (requestType) {
		case hash_json:
			int clientVersion = 0;
			// response.setContentType("t/json; charset=utf-8");
			if (request.getHeader("X-Client") != null)
				clientVersion = Integer.parseInt(request.getHeader("X-Client"));
			else {
				if (request.getParameter("xclient") != null)
					clientVersion = Integer.parseInt(request
							.getParameter("xclient"));
			}
			doResultJson(response,request, forHtml, rlt, clientVersion);
			break;
		case hash_wap1:
			doResultWap1(request, response, rlt);
			break;
		case hash_wap2:
			doResultWap2(request, response, rlt);
			break;
		case hash_h4:
		case hash_do:
		case hash_htm:
			doResultHtml(request, response, rlt);
			break;
		case hash_webkit:
			doResultWebkit(request, response, rlt);
			break;
		}
	}

	private void writeCookie(List<Cookie> cookies, Cookie cookie) {
		for (int i = 0; i < cookies.size(); i++) {
			if (cookies.get(i).getName().equals(cookie.getName())) {
				cookies.remove(i);
				break;
			}
		}
		cookies.add(cookie);
	}

	private void doResultJson(HttpServletResponse response,HttpServletRequest request, boolean forHtml,
			Object rlt, int clientVersion) throws Exception {
		//response.setContentType(forHtml ? "text/json;charset=utf-8"
		//		: "application/octet-stream");
		if (rlt != null) {
			if (rlt instanceof byte[]) {
				byte[] body = (byte[]) rlt;
				response.setContentLength(body.length);
				response.getOutputStream().write(body);
			} else {
				byte[] body = null;
				if (forHtml) {
					body = gson.toJson(rlt).getBytes("utf-8");
//					String encoding=request.getHeader("Accept-Encoding");
//					if(body.length>2000 && encoding!=null &&
//							encoding.contains("gzip"))
//					{
//						
//						body=gzip(body);
//						response.setHeader("content-encoding", "gzip");
//					}
				} else {
					JsonObject object = JsonObject.ObjToJsonObject(rlt);
					object.setVersion(clientVersion);
					body = object.toByteArray();
				}
				response.setContentLength(body.length);
				response.getOutputStream().write(body);
			}

		}

	}
	private byte[] gzip(byte[] data)throws Exception
	{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();  
        GZIPOutputStream gzip = new GZIPOutputStream(bout);  
        gzip.write(data);  
        gzip.flush();  
        gzip.close();
        return bout.toByteArray();
	}
	private void doResultHtml(HttpServletRequest request,
			HttpServletResponse response, Object rlt) throws Exception {
		if (rlt != null && rlt instanceof RedirectInterface) {
			RedirectInterface ri = (RedirectInterface) rlt;
			if (ri.getRedirect() != null) {
				request.setAttribute("resultData", rlt);
				request.getRequestDispatcher(
						"/WEB-INF/" + ri.getRedirect() + ".jsp").forward(
						request, response);
			}
		} else {
			String redirect=JsonAction.getRedirect();
			if (redirect != null
					&& redirect.startsWith("http")) {
				response.sendRedirect(redirect);
			} else {
				request.setAttribute("resultData", rlt);
				Map<String,Object> objects=JsonAction.getObjects();
				if(objects!=null)
				{
					for(Entry<String,Object>  v:objects.entrySet())
					{
						request.setAttribute(v.getKey(), v.getValue());
					}
				}
				if(redirect.startsWith("/"))
					request.getRequestDispatcher(redirect+".jsp")
							.forward(request, response);
				else
					request.getRequestDispatcher(
						"/WEB-INF/" + redirect + ".jsp")
						.forward(request, response);
			}
		}
	}

	private void doResultWebkit(HttpServletRequest request,
			HttpServletResponse response, Object rlt) throws Exception {
		if (rlt != null && rlt instanceof RedirectInterface) {
			RedirectInterface ri = (RedirectInterface) rlt;
			if (ri.getRedirect() != null) {
				request.setAttribute("resultData", rlt);
				request.getRequestDispatcher(
						"/WEB-INF/" + ri.getRedirect() + "-wk.jsp").forward(
						request, response);
			}
		} else {
			if (JsonAction.getRedirect() != null
					&& JsonAction.getRedirect().startsWith("http")) {
				response.sendRedirect(JsonAction.getRedirect());
			} else {

				request.setAttribute("resultData", rlt);
				request.getRequestDispatcher(
						"/WEB-INF/" + JsonAction.getRedirect() + "-wk.jsp")
						.forward(request, response);
			}

		}
	}

	private void doResultWap1(HttpServletRequest request,
			HttpServletResponse response, Object rlt) throws Exception {
		if (rlt != null && rlt instanceof RedirectInterface) {

			RedirectInterface ri = (RedirectInterface) rlt;

			if (ri.getRedirect() != null) {
				request.setAttribute("resultData", rlt);
				request.getRequestDispatcher(
						"/WEB-INF/" + ri.getRedirect() + "-wap1.jsp").forward(
						request, response);
			}
		} else {
			if (JsonAction.getRedirect() != null
					&& JsonAction.getRedirect().startsWith("http")) {
				response.sendRedirect(JsonAction.getRedirect());
			} else {
				request.setAttribute("resultData", rlt);
				request.getRequestDispatcher(
						"/WEB-INF/" + JsonAction.getRedirect() + "-wap1.jsp")
						.forward(request, response);
			}

		}

	}

	private void doResultWap2(HttpServletRequest request,
			HttpServletResponse response, Object rlt) throws Exception {
		if (rlt != null && rlt instanceof RedirectInterface) {
			RedirectInterface ri = (RedirectInterface) rlt;
			if (ri.getRedirect() != null) {
				request.setAttribute("resultData", rlt);
				request.getRequestDispatcher(
						"/WEB-INF/" + ri.getRedirect() + "-wap2.jsp").forward(
						request, response);
			}
		} else {
			if (JsonAction.getRedirect() != null
					&& JsonAction.getRedirect().startsWith("http")) {
				response.sendRedirect(JsonAction.getRedirect());
			} else {

				request.setAttribute("resultData", rlt);
				request.getRequestDispatcher(
						"/WEB-INF/" + JsonAction.getRedirect() + "-wap2.jsp")
						.forward(request, response);
			}

		}

	}

	// private Object getError(String msg) {
	// ErrorInterface error = null;
	// try {
	// error = (ErrorInterface) errorClass.newInstance();
	//
	// } catch (Exception e) {
	// error = new Error();
	// }
	// error.setMessage(msg);
	// return error;
	// }
	private void initService()
	{
		try
		{
			BufferedReader reader=new BufferedReader(new InputStreamReader(classLoader.getResourceAsStream("service.properties")));
			String clzName=null;
			while((clzName=reader.readLine())!=null&&!clzName.trim().equals(""))
			{
				ServiceManager.addService(clzName);
				
			}
		}catch(Exception e)
		{
			
		}
	}
	private void initProperties()
	{
		Properties prop=new Properties();
		try {
			prop.load(classLoader.getResourceAsStream("config.properties"));
			Field[] fields=Class.forName((String)prop.get("class")).getDeclaredFields();
			for(Field field:fields)
			{
				if(Modifier.isFinal(field.getModifiers()) || !Modifier.isPublic(field.getModifiers())
						|| !Modifier.isStatic(field.getModifiers()))
				{
					continue;
				}
				String v=(String)prop.get(field.getName());
				if(v==null) continue;
				switch(field.getType().getName().hashCode())
				{
					case FieldType.BOOL:
						field.set(null, v.equalsIgnoreCase("true"));
						break;
					case FieldType.INT:
						field.set(null, Integer.parseInt(v));
						break;
					case FieldType.STRING:
						
						field.set(null, (String)prop.get(field.getName()));
						break;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
		}
	}
	@Override
	public void init(ServletConfig config) throws ServletException {
		this.classLoader = Thread.currentThread().getContextClassLoader();
		gzip=("true".equalsIgnoreCase(config.getInitParameter("gzip")));
		initProperties();
		initService();
		this.initService(config.getInitParameter("service"));
		tmpFilePath = new File(config.getInitParameter("tmp_dir"));
		try {
			String errorClassName = config.getInitParameter("error_class");
			if (errorClassName != null) {
				errorClass = Class.forName(errorClassName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		String maxSize = config.getInitParameter("max_size");
		if (maxSize != null)
			DEFAULT_SIZE_MAX = Integer.parseInt(maxSize);
		String pack = config.getInitParameter("actions");
		if (pack != null) {
			initActionPacks(pack);
		}
		super.init(config);
	}

	private void initActionPacks(String pack) {
		actionPacks = pack.split(";");

	}

	static ClassLoader classLoader = null;

	// private void initClassLoader() throws Exception {
	// URL url1 = new URL("file:/"
	// + this.getServletContext().getRealPath("/WEB-INF/classes")
	// + "/");
	// System.out.println(url1);
	// classLoader = new URLClassLoader(new URL[] { url1 }, this.getClass()
	// .getClassLoader());
	//
	// }
	private static String getActionName(String path) {
		// int index=path.lastIndexOf("/");
		// if(index<0) return path;
		// else
		// return path.substring(index+1);
		return path.replace('/', '.');
	}

	public static JsonAction getAction(String path) throws Exception {
		// String path=request.getPathInfo();
		String actionName = getActionName(path);
		Class<JsonAction> clz = mapActions.get(actionName);
		if (clz == null) {
			clz = findClass(actionName);
			if (clz != null)
				mapActions.put(actionName, clz);
		}
		if (clz != null)
			return (JsonAction) creator.createInstance(clz);
		throw new Exception("action not found:" + path);
	}

	private static Class findClass(String path) {
		int last = path.lastIndexOf('.');
		if(last>0)
		{
			path = path.substring(0, last);
			if (path.charAt(0) != '.')
				path = "." + path;
		}
		// String clzPath = path.replaceAll("/", ".");

		for (int i = 0; i < actionPacks.length; i++) {
			String clzName = actionPacks[i] + path + "Action";
			// System.out.println(clzName);
			try {
				return classLoader.loadClass(clzName);
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
		return null;
	}
	
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		ServiceManager.stopAllService();
		super.destroy();
	}


	static final Logger logger = Logger.getLogger(ActionServlet.class);

	public void initService(String classNames) {
		if (classNames == null)
			return;
		String[] szClass = classNames.split(";");
		if (szClass == null || szClass.length == 0)
			return;
		for (String clzName : szClass) {
			ServiceManager.addService(clzName);
		}
	}
	private String getIpAddr(HttpServletRequest request) {
		String ipAddress = null;
		// ipAddress = this.getRequest().getRemoteAddr();
		ipAddress = request.getHeader("x-forwarded-for");
		if (ipAddress == null || ipAddress.length() == 0
				|| "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0
				|| "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0
				|| "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getRemoteAddr();
		}

		// ���������������������������������������������IP������������������IP,������IP������','������
		if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length()
															// = 15
			if (ipAddress.indexOf(",") > 0) {
				ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
			}
		}
		return ipAddress;
	}
}
