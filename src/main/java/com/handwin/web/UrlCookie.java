package com.handwin.web;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;

public class UrlCookie {
	public static List<Cookie> getUrlCookies(HttpServletRequest request) {
		String uri=request.getRequestedSessionId();
		if(uri==null) return null;
		List<Cookie> cookies = new ArrayList<Cookie>();
		String cookieString = "";
		try {
			cookieString=URLDecoder.decode(
			uri.substring(uri.indexOf(';') + 1), "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String[] strCookies = cookieString.split("&");
		if (strCookies != null) {
			for (int i = 0; i < strCookies.length; i++) {
				String[] strCookie = strCookies[i].split("=");
				if (strCookie.length ==2) {
					//cookies.add(new Cookie(strCookie[0], strCookie[1]));
					addCookie(cookies,new Cookie(strCookie[0], strCookie[1]));
				}
			}
		}
		return cookies;

	}

	public static String getCookieValue(HttpServletRequest request, String name) {
		if (request.getCookies() != null) {
			for (int i = 0; i < request.getCookies().length; i++) {
				if (request.getCookies()[i].getName().equals(name)){
					if(!StringUtils.isBlank(request.getCookies()[i].getValue()))
						return request.getCookies()[i].getValue();
				}
			}
		}
		try {
			List<Cookie> cookies = getUrlCookies(request);
			if(cookies!=null)
			for (Cookie c : cookies) {
				if (c.getName().equals(name))
					return c.getValue();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	private static void addCookie(List<Cookie> cookies,Cookie cookie)
	{
		for(int i=0;i<cookies.size();i++)
		{
			if(cookies.get(i).getName().equals(cookie.getName()))
			{
				cookies.remove(i);
				break;
			}
		}
		cookies.add(cookie);
	}
	public static void main(String[] args) {
		List<Cookie> cookies=new ArrayList<Cookie>();
		for(int i=0;i<10;i++)
			addCookie(cookies,new Cookie("name","value"));
		addCookie(cookies,new Cookie("name","value"));
		System.out.println(cookiesToString(cookies));
	}
	public static void addCookie(HttpServletRequest request,Cookie cookie)
	{
		List<Cookie> cookies=(List<Cookie>)request.getAttribute("cookies");
		if(cookies==null) cookies=new ArrayList<Cookie>();
		addCookie(cookies,cookie);
			request.setAttribute("cookies", cookies);
	}
	public static String cookiesToString(List<Cookie> cookies)
	{
		StringBuffer cookieValue=new StringBuffer();
		if (cookies != null && cookies.size() > 0) {
			cookieValue.append(";jsessionid=");
			boolean first=true;
			for (Cookie cookie : cookies) {
				if(!first)
				{
					cookieValue.append("%26");
				}else
				{
					first=false;
				}
				cookieValue.append(cookie.getName());
				cookieValue.append("%3d");
				//cookieValue.append(cookie.getValue());
				try {
					cookieValue.append(java.net.URLEncoder.encode(cookie
							.getValue(), "utf-8"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return cookieValue.toString();
		}else
			return "";
		
		
		
	}

}

