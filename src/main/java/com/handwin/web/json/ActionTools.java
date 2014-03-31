package com.handwin.web.json;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.NameValuePair;


public class ActionTools {
	public static ThreadLocal<ClientInfo>   clientInfo=new ThreadLocal<ClientInfo>();
	public static void setClient(HttpServletRequest request,
			HttpServletResponse response)
	{
		clientInfo.set(new ClientInfo(request,response));
	}
	public static ClientInfo getClientInfo()
	{
		return clientInfo.get();
	}
	public static NameValuePair[] toParameter(Object obj)
	{
		Field[] fields=obj.getClass().getDeclaredFields();
		List<NameValuePair> pair=new ArrayList<NameValuePair>();
		for(Field f:fields)
		{
			FormField ff=f.getAnnotation(FormField.class);
			if(ff!=null)
			{
				f.setAccessible(true);
				try {
					Object v=f.get(obj);
					if(v!=null)
					{
						System.out.println(v);
					}
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return pair.toArray(new NameValuePair[0]);
	}
	private void fieldToNameValuePair(Object obj,Field f,List<NameValuePair> pair)
	{
		
	}
	public static void main(String[] args) {
		toParameter(new TestToParameter());
	}
}
