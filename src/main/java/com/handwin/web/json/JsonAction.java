package com.handwin.web.json;

import java.lang.reflect.Field;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang.StringUtils;

public abstract class JsonAction implements InputError,FieldType{
	private static final ThreadLocal<String> threadLocalRedirect = new ThreadLocal<String>();
	private static final ThreadLocal<List<Cookie>> threadLocalCookie = new ThreadLocal<List<Cookie>>();
	private static final ThreadLocal<Long> threadLocalExpire = new ThreadLocal<Long>();
	private static final ThreadLocal<Map<String,Object>> threadLocalResult=new ThreadLocal<Map<String,Object>>();
	public static void clear()
	{
		if(getWriteCookies()!=null)
			getWriteCookies().clear();
		if(threadLocalExpire.get()!=null)
			threadLocalExpire.set(0l);
	}
	public static String getRedirect()
	{
		return threadLocalRedirect.get();
	}
	public static List<Cookie> getWriteCookies()
	{
		return threadLocalCookie.get();
	}
	public static void putObject(String key,Object value)
	{
		Map<String,Object> map=threadLocalResult.get();
		if(map==null) map=new HashMap<String,Object>();
		map.put(key, value);
		threadLocalResult.set(map);
	}
	public static Map<String,Object> getObjects()
	{
		return threadLocalResult.get();
	}
	public static void setRedirect(String value)
	{
		threadLocalRedirect.set(value);
	}
	public static void addCookie(Cookie cookie)
	{
		List<Cookie> cookies=threadLocalCookie.get();
		if(cookies==null){
			cookies=new ArrayList<Cookie>();
			
		}
		cookies.add(cookie);
		threadLocalCookie.set(cookies);
		
	}
	private HttpSession session;
	private Cookie[] cookies;
	private Map<String,String> headers=new HashMap<String,String>();
	//private Map<String,Object> attributes=new HashMap<String,Object>();
	private URL url;
	private String referer;
	public final String getReferer() {
		return referer;
	}
	public final void setReferer(String referer) {
		this.referer = referer;
	}
	public abstract Object execute();
	public abstract Object runtimeException(RuntimeException e);
	public boolean needTransaction()
	{
		return true;
	}
	public Object onInputError(String errorMsg)
	{
		return new Error(errorMsg);
	}
	private String remoteAddr;
	private String realPath;
	private boolean formValid=false;
	private String validException=null;
	private HttpServletRequest request;
	private HttpServletResponse response;
	public final HttpServletRequest getRequest() {
		return request;
	}
	public final void setRequest(HttpServletRequest request) {
		
		this.request = request;
	}
	public  final HttpServletResponse getResponse() {
		return response;
	}
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}
	public final void setHeader(String name,String value)
	{
		headers.put(name.toLowerCase(), value);
	}
	public final String getHeader(String name)
	{
		return headers.get(name);
	}
	public final Cookie[] getCookies() {
		return cookies;
	}
	public final String getCookieValue(String name)
	{
		if(cookies==null) return null;
		for(int i=0;i<cookies.length;i++)
		{
			if(cookies[i].getName().equals(name))
				return cookies[i].getValue();
		}
		return null;
	}
	public final String getValidException() {
		
		return validException;
	}
	public final void setValidException(String validException) {
		this.validException = validException;
	}
	public final boolean isFormValid() {
		return formValid;
	}
	public final void setFormValid(boolean formValid) {
		this.formValid = formValid;
	}
	
	public final void parseInput(List items,Field[] fields)throws Exception
	{
	
		Map<String,List> mapValue=this.getFormValue(items);
		for(Field f:fields)
		{
			
			if(f.isAnnotationPresent(FormField.class))
			{
				//System.out.println(f.getName());
				FormField ff=f.getAnnotation(FormField.class);
				if(ff.type()==FormField.FieldType.PARAM){
					List lst=mapValue.get(f.getName());
					switch(f.getType().getName().hashCode()){
					case INT:
						if(lst!=null)
							checkIntInput(f,(String[])lst.toArray(new String[0]));
						else
							checkIntInput(f,null);
						break;
					case STRING:
						if(lst!=null)
							checkStringInput(f,(String[])lst.toArray(new String[0]));
						else
							checkStringInput(f,null);
						break;
					case INT_ARRAY:
						if(lst!=null)
							checkIntArrayInput(f,(String[])lst.toArray(new String[0]));
						else
							checkIntArrayInput(f,null);
						break;
					case STRING_ARRAY:
						if(lst!=null)
							checkStringArrayInput(f,(String[])lst.toArray(new String[0]));
						else
							checkStringArrayInput(f,null);
						break;
					case LONG:
						if(lst!=null)
							checkLongInput(f,(String[])lst.toArray(new String[0]));
						else
							checkLongInput(f,null);
						break;
					case LONG_ARRAY:
						if(lst!=null)
							checkLongArrayInput(f,(String[])lst.toArray(new String[0]));
						else
							checkLongArrayInput(f,null);
						break;
					case FLOAT:
						if(lst!=null)
							checkFloatInput(f,(String[])lst.toArray(new String[0]));
						else
							checkFloatInput(f,null);
						break;
					case FLOAT_ARRAY:
						if(lst!=null)
							checkFloatArrayInput(f,(String[])lst.toArray(new String[0]));
						else
							checkFloatArrayInput(f,null);
						break;
					case FORM_FILE:
						if(lst!=null)
						{
							FormFile[] files=null;
							try
							{
								files=(FormFile[])lst.toArray(new FormFile[0]);
							}catch(Exception e)
							{
								
							}
							checkFileInput(f,files);
						}
						else
							checkFileInput(f,null);
						break;
					case FORM_FILE_ARRAY:	
						if(lst!=null)
							checkFileArrayInput(f,(FormFile[])lst.toArray(new FormFile[0]));
						else
							checkFileArrayInput(f,null);
						break;
					case BOOL:
					case BOOLEAN:
						checkBooleanInput(f,(String[])lst.toArray(new String[0]));
						break;
					case BOOL_ARRAY:
						checkBooleanArrayInput(f,(String[])lst.toArray(new String[0]));
						break;
					case DATE:
						checkDateInput(f, (String[])lst.toArray(new String[0]));
						break;
					case DATE_ARRAY:
						checkDateArrayInput(f, (String[])lst.toArray(new String[0]));
						break;
					}
				}
			}
		}
		
	}
	private final Map<String,List> getFormValue(List items)throws Exception
	{
		if(items==null) return null;
		Map<String,List> mapValue=new HashMap<String,List>();
		
		for(int i=0;i<items.size();i++)
		{
			FileItem item=(FileItem)items.get(i);
			//System.out.println(item.getFieldName());
			List obj=mapValue.get(item.getFieldName());
			boolean objIsNull=obj==null;
			if(obj==null) obj=new ArrayList();
			if(item.isFormField())
			{
				(obj).add(item.getString("utf-8"));
			}else
				(obj).add(new FormFile(item));
			if(objIsNull)
				mapValue.put(item.getFieldName(), obj);
		}
		//System.out.println("item:"+mapValue.get("file1"));
		return mapValue;
	}
	public final void checkFileInput(Field field,FormFile[] item)throws Exception
	{
		FormField ff=field.getAnnotation(FormField.class);
		if(ff.required() && (item==null || item.length==0))
		{
			throw new Exception(getInputError(input_error_reqired,new String[]{ff.name()}));
		}
		//getSetter(field,FormFile.class).invoke(this, item[0]);
		if(item!=null ){
			if(item.length >0)
			setField(field,item[0],this);
		}
	}
	public final void checkFileArrayInput(Field field,FormFile[] item)throws Exception
	{
		FormField ff=field.getAnnotation(FormField.class);
		if(ff.required() && (item==null || item.length==0))
		{
			throw new Exception(getInputError(input_error_reqired,new String[]{ff.name()}));
		}
		//getSetter(field,FormFile[].class).invoke(this, new Object[]{item});
		setField(field,item,this);
	}
//	public final void parseAttribute(HttpServletRequest request){
//		this.request=request;
//		Enumeration<String> names = request.getAttributeNames();
//		while(names.hasMoreElements()){
//			String name=names.nextElement();
//			attributes.put(name, request.getAttribute(name));
//		}
//	}
	public final void parseInput(HttpServletRequest request,Field[] fields) throws Exception
	{
	
		for(Field f:fields)
		{
			
			if(f.isAnnotationPresent(FormField.class))
			{
				FormField ff=f.getAnnotation(FormField.class);
				if(ff.type()==FormField.FieldType.PARAM){
					switch(f.getType().getName().hashCode()){
					case INT:
						checkIntInput(f,request.getParameterValues(f.getName()));
						break;
					case STRING:
						this.checkStringInput(f, request.getParameterValues(f.getName()));
						break;
					case INT_ARRAY:
						checkIntArrayInput(f, request.getParameterValues(f.getName()));
						break;
					case STRING_ARRAY:
						checkStringArrayInput(f, request.getParameterValues(f.getName()));
						break;
					case LONG:
						checkLongInput(f, request.getParameterValues(f.getName()));
						break;
					case LONG_ARRAY:
						checkLongArrayInput(f, request.getParameterValues(f.getName()));
						break;
					case FLOAT:
						checkFloatInput(f, request.getParameterValues(f.getName()));
						break;
					case FLOAT_ARRAY:
						checkFloatArrayInput(f, request.getParameterValues(f.getName()));
						break;
					case FORM_FILE:
							checkFileInput(f,null);
						break;
					case FORM_FILE_ARRAY:	
						checkFileArrayInput(f,null);
						break;
					case BOOL:
					case BOOLEAN:
						checkBooleanInput(f,request.getParameterValues(f.getName()));
						break;
					case BOOL_ARRAY:
						checkBooleanArrayInput(f,request.getParameterValues(f.getName()));
						break;
					case DATE:
						checkDateInput(f, request.getParameterValues(f.getName()));
						break;
					case DATE_ARRAY:
						checkDateArrayInput(f, request.getParameterValues(f.getName()));
						break;
					}
				}
			}
		}
	}
	private void checkValueNoArray(Field f,String[] value) throws Exception{
		switch(f.getType().getName().hashCode()){
		case INT:
			checkIntInput(f,value);
			break;
		case STRING:
			this.checkStringInput(f, value);
			break;
		case LONG:
			checkLongInput(f, value);
			break;
		case FLOAT:
			checkFloatInput(f, value);
			break;
		case BOOL:
		case BOOLEAN:
			checkBooleanInput(f,value);
			break;
		case DATE:
			checkDateInput(f, value);
			break;

		}
	}
	public final void parseHead(HttpServletRequest request,Field[] fields) throws Exception{
		//Field[] fields=this.getClass().getDeclaredFields();
		for(Field f:fields){
			if(f.isAnnotationPresent(FormField.class)){
				FormField ff=f.getAnnotation(FormField.class);
				if(ff.type()==FormField.FieldType.HEAD||ff.type()==FormField.FieldType.COOKIE_OR_HEAD){
					String key=f.getName();
					if(!StringUtils.isBlank(ff.key())){
						key=ff.key();
					}
					String value=request.getHeader(key);
					checkValueNoArray(f,value==null?null:new String[]{value});
				}
			}
		}
	}
	public final void parseCookie(Cookie[] cookies,Field[] fields) throws Exception{
		
		
		for(Field f:fields){
			if(f.isAnnotationPresent(FormField.class)){
				FormField ff=f.getAnnotation(FormField.class);
				if(ff.type()==FormField.FieldType.COOKIE||ff.type()==FormField.FieldType.COOKIE_OR_HEAD){
					String key=f.getName();
					if(!StringUtils.isBlank(ff.key())){
						key=ff.key();
					}
					String value=getCookieValue(cookies,key);
					checkValueNoArray(f, value==null?null:new String[]{value});
				}
			}
		}
	}
	private String getCookieValue(Cookie[] cookies,String name){
		if(cookies==null||cookies.length==0)
			return null;
		for (int i = 0; i < cookies.length; i++) {
			if(cookies[i].getName().equals(name))
				return cookies[i].getValue();
		}
		return null;
	}
	public final void checkFloatInput(Field field,String[] value) throws Exception
	{
		FormField ff=checkRequired(field,value);
		
		float v=0.00f;
		try
		{
			
			v=Float.parseFloat(value[0]);
			//getSetter(field,float.class).invoke(this, v);
			setField(field,v,this);
		}catch(Exception e)
		{
			e.printStackTrace();
			throw new Exception(this.getInputError(input_error_floatnum, new String[]{ff.name()}));
		}
		FloatRange floatRange=field.getAnnotation(FloatRange.class);
		if(floatRange!=null)
		{
			if(v<floatRange.min() || v>floatRange.max())
				throw new Exception(getInputError(input_error_floatrange,new String[]{ff.name(),Float.toString(floatRange.min()) ,Float.toString(floatRange.max())}));
		}
	}
	public final void checkFloatArrayInput(Field field,String[] value)throws Exception
	{
		FormField ff=checkRequired(field,value);
		if(value!=null)
		{
			float[] floatArray=new float[value.length];
			FloatRange floatRange=field.getAnnotation(FloatRange.class);
			for(int i=0;i<value.length;i++)
			{
				try
				{
					floatArray[i]=Float.parseFloat(value[i]);
				}catch(Exception e)
				{
					throw new Exception(getInputError(input_error_integer,new String[]{ff.name()}));
				}
				if(floatRange!=null)
				{
					if(floatArray[i]<floatRange.min() || floatArray[i]>floatRange.max())
					{
						//throw new Exception(ff.name()+"必须在"+floatRange.min()+"和"+floatRange.max()+"之间");
						throw new Exception(getInputError(input_error_floatrange,new String[]{ff.name(),String.valueOf(floatRange.min()),String.valueOf(floatRange.max())}));
					}
				}
			}
			//getSetter(field,float[].class).invoke(this, floatArray);
			setField(field,floatArray,this);
		}
	}
	public final void checkLongArrayInput(Field field,String[] value)throws Exception
	{
		FormField ff=checkRequired(field,value);
		if(value!=null)
		{
			long[] longArray=new long[value.length];
			LongRange longRange=field.getAnnotation(LongRange.class);
			for(int i=0;i<value.length;i++)
			{
				try
				{
					longArray[i]=Long.parseLong(value[i]);
				}catch(Exception e)
				{
					throw new Exception(getInputError(input_error_longtype,new String[]{ff.name()}));
				}
				if(longRange!=null)
				{
					if(longArray[i]<longRange.min() || longArray[i]>longRange.max())
					{
						throw new Exception(getInputError(input_error_longrange,new String[]{ff.name(),String.valueOf(longRange.min()),String.valueOf(longRange.max())}));
					}
				}
			}
			//getSetter(field,long[].class).invoke(this, longArray);
			setField(field,longArray,this);
		}
	}
	private final String getInputError(String error,String[] args)
	{
		String rlt=error;
		if(args!=null && args.length>0)
		for(int i=0;i<args.length;i++)
		{
			rlt=rlt.replace("{"+i+"}", args[i]);
		}
		//System.err.println(this.getClass().getName());
		return rlt;
	}
	
	public final void checkLongInput(Field field,String[] value) throws Exception
	{
		FormField ff=checkRequired(field,value);
		
		long v=0;
		try
		{
			
			if(value!=null && value.length>0)
			{
				v=Long.parseLong(value[0]);
				//getSetter(field,long.class).invoke(this, v);
				setField(field,v,this);
			}
		}catch(Exception e)
		{
			e.printStackTrace();
			throw new Exception(getInputError(input_error_longtype,new String[]{ff.name()}));
		}
		LongRange longRange=field.getAnnotation(LongRange.class);
		if(longRange!=null)
		{
			if(v<longRange.min() || v>longRange.max())
			{
				throw new Exception(getInputError(input_error_longrange,new String[]{ff.name(),String.valueOf(longRange.min()),String.valueOf(longRange.max())}));
			}
		}
	}
	public final void checkBooleanInput(Field field,String[] value)throws Exception
	
	{
		
		FormField ff=checkRequired(field,value);
		if(value!=null && value.length>0)
		{
			setField(field,"true".equalsIgnoreCase(value[0]),this);
		}
		
	}
	public final void checkBooleanArrayInput(Field field,String[] value)throws Exception
	
	{
		FormField ff=checkRequired(field,value);
		if(value!=null && value.length>0)
		{
			boolean[] rlt=new boolean[value.length];
			for(int i=0;i<value.length;i++)
			{
				rlt[i]="true".equalsIgnoreCase(value[i]);
			}
			setField(field,rlt,this);
		}
		
	}
	public final void checkStringArrayInput(Field field,String[] value)throws Exception
	{
		FormField ff=checkRequired(field,value);
		if(value!=null)
		{
			StringLength stringLen=field.getAnnotation(StringLength.class);
			StringPattern stringPattern=field.getAnnotation(StringPattern.class);
			Pattern p=null;
			if(stringPattern!=null)
				p=Pattern.compile(stringPattern.pattern(),Pattern.DOTALL);
			for(int i=0;i<value.length;i++)
			{
				if(stringLen!=null)
				{
					if(value[i].length()<stringLen.min() || value[i].length()>stringLen.max())
					{
						throw new Exception(getInputError(input_error_strlen,new String[]{ff.name(),String.valueOf(stringLen.min()),String.valueOf(stringLen.max())}));
						//throw new Exception(ff.name()+"长度必须在"+stringLen.min()+"和"+stringLen.max()+"之间");
					}
				}
				if(stringPattern!=null && !StringUtils.isBlank(value[i]))
				{
					if(!p.matcher(value[i]).matches())
					{
						throw new Exception(getInputError(input_error_strarraypattern,new String[]{ff.name(),stringPattern.name(),value[i]}));
					}
				}
			}
			//getSetter(field,String[].class).invoke(this, new Object[]{value});
			setField(field,value,this);
		}
	}


	private FormField checkRequired(Field field,String[] value) throws Exception
	{
		FormField ff=field.getAnnotation(FormField.class);
		if(ff.required() && (value==null || value.length==0 || StringUtils.isBlank(value[0])))
		{
			throw new Exception(getInputError(input_error_reqired,new String[]{ff.name()}));
		}
		return ff;
		
	}
	public final void checkStringInput(Field field,String[] value)throws Exception
	{
		FormField ff=checkRequired(field,value);
		if(value!=null)
		{
			checkStringLen(field,value[0],ff.name());
			checkStringPattern(field,value[0],ff.name());
			//getSetter(field,String.class).invoke(this, value[0]);
			setField(field,value[0],this);
		}
	}
	public final void checkDateInput(Field field,String[] value)throws Exception{
		FormField ff=checkRequired(field,value);
		if(value!=null && value.length>0 && !StringUtils.isBlank(value[0])){
			DatePattern pattern=field.getAnnotation(DatePattern.class);
				SimpleDateFormat format=null;
			    if(pattern==null){
			    	format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			    }else{
			    	format=new SimpleDateFormat(pattern.pattern());
			    }
			    try {
			    	setField(field,format.parse(value[0]),this);
				} catch (ParseException e) {
					// TODO: handle exception
					throw new Exception(getInputError(input_error_date,new String[]{ff.name()}));
				}
		}
	}
	public final void checkDateArrayInput(Field field,String[] value)throws Exception{
		FormField ff=checkRequired(field,value);
		if(value!=null){
			DatePattern pattern=field.getAnnotation(DatePattern.class);
				SimpleDateFormat format=null;
			    if(pattern==null){
			    	format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			    }else{
			    	format=new SimpleDateFormat(pattern.pattern());
			    }
			    Date[] dates=new Date[value.length];
			    for(int i=0;i<value.length;i++){
				    try {
				    	dates[i]=format.parse(value[i]);
					} catch (ParseException e) {
						// TODO: handle exception
						throw new Exception(getInputError(input_error_date,new String[]{ff.name()}));
					}
				}
			    setField(field,dates,this);
		}
	}
//	private Method getSetter(Field field,Class clz) throws Exception
//	{
//		String methodName="set"+field.getName().substring(0, 1).toUpperCase()+field.getName().substring(1);
//		Method method=this.getClass().getDeclaredMethod(methodName, new Class[]{clz});
//		//if(method==null)
//		//	method=this.getClass().getDeclaredMethod(methodName, new Class[]{Integer.class});
//		if(method==null)
//			throw new Exception("字段"+field.getName()+"不能访问");
//		return method;
//		
//	}
	private void setField(Field field,Object value,Object obj)
	{
		field.setAccessible(true);
		try {
			field.set(obj, value);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void checkStringPattern(Field field,String value,String name)throws Exception
	{
		FormField ff=field.getAnnotation(FormField.class);
		StringPattern pattern=field.getAnnotation(StringPattern.class);
		if(pattern!=null && !StringUtils.isBlank(value))
		{
			Pattern p=Pattern.compile(pattern.pattern(),Pattern.DOTALL);
			if(!p.matcher(value).matches()){
				throw new Exception(getInputError(input_error_strpattern,new String[]{ff.name(),pattern.name()}));
			}
		}
	}
	private void checkStringLen(Field field,String value,String name)throws Exception
	{
		FormField ff=field.getAnnotation(FormField.class);
		StringLength len=field.getAnnotation(StringLength.class);
		if(len!=null)
		{
			if(StringUtils.isBlank(value)) 
			{
				//throw new Exception(getInputError(reqired,new String[]{name}));
				return;
			}
			if(value.length()<len.min() || value.length()>len.max())
				throw new Exception(getInputError(input_error_strlen,new String[]{name,Integer.toString(len.min()),Integer.toString(len.max())}));
		}
	}
	public final void checkIntInput(Field field,String[] value) throws Exception
	{
		FormField ff=checkRequired(field,value);
		
		int v=0;
		try
		{
			if(value!=null && value.length>0&&!StringUtils.isBlank(value[0]))
			{
				v=Integer.parseInt(value[0]);
				setField(field,v,this);
			}
			//getSetter(field,int.class).invoke(this, v);
			
		}catch(Exception e)
		{
			throw new Exception(getInputError(input_error_integer,new String[]{ff.name()}));
		}
		IntRange intRange=field.getAnnotation(IntRange.class);
		if(intRange!=null)
		{
			if(v<intRange.min() || v>intRange.max())
			{
				throw new Exception(getInputError(input_error_intrange,new String[]{ff.name(),Integer.toString(intRange.min()),Integer.toString(intRange.max())}));
			}
		}
	}
	public final void checkIntArrayInput(Field field,String[] value)throws Exception
	{
		FormField ff=checkRequired(field,value);
		if(value!=null)
		{
			int[] intArray=new int[value.length];
			IntRange intRange=field.getAnnotation(IntRange.class);
			for(int i=0;i<value.length;i++)
			{
				try
				{
					intArray[i]=Integer.parseInt(value[i]);
				}catch(Exception e)
				{
					throw new Exception(getInputError(input_error_integer,new String[]{ff.name()}));
				}
				if(intRange!=null)
				{
					if(intArray[i]<intRange.min() || intArray[i]>intRange.max())
						throw new Exception(getInputError(input_error_intrange,new String[]{ff.name(),Integer.toString(intRange.min()),Integer.toString(intRange.max())}));
				}
			}
			//getSetter(field,int[].class).invoke(this, intArray);
			setField(field,intArray,this);
		}
	}
	public static void main(String[] args)
	{	
		//System.out.println(getInputError(floatrange,new String[]{"nn","1","2"}));
		System.out.println(Date.class.getName().hashCode());
		System.out.println(Date[].class.getName().hashCode());
	}
	public final HttpSession getSession() {
		return session;
	}
//	public final Object getAttribute(String name){
//		return attributes.get(name);
//	}
	public final void setSession(HttpSession session) {
		this.session = session;
	}
	public final void setCookies(Cookie[] cookies) {
		this.cookies = cookies;
	}
	
	public final URL getUrl() {
		return url;
	}
	public final void setUrl(URL url) {
		this.url = url;
	}
	public final String getRemoteAddr() {
		return remoteAddr;
	}
	public final void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}
	public final String getRealPath() {
		return realPath;
	}
	public final void setRealPath(String realPath) {
		this.realPath = realPath;
	}
	

//		System.out.println(float[].class.getName().hashCode());
//		System.out.println(float.class.getName().hashCode());
//		System.out.println(long.class.getName().hashCode());
//		try{  
//			   float data = 0.0345f;  
//			   NumberFormat famatter = NumberFormat.getNumberInstance(java.util.Locale.CHINA);  
//			   famatter.setMaximumFractionDigits(3);
//			   String sData  = famatter.format(data);         
//			   System.out.println(sData);  
//			   float value = famatter.parse(sData).floatValue();  
//			   System.out.print(value);  
//			  }catch(ParseException   e){  
//			   e.printStackTrace();  
//			  }  
//			 }  

}
