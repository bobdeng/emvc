/*
 * Created on 2006-3-30
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.handwin.db;

import java.util.Hashtable;

import javax.servlet.http.HttpSession;

public class HttpSessions {
private static Hashtable sessions=new Hashtable();
public static void addSession(String id,HttpSession session)
{
	sessions.put(id,session);
}
public static HttpSession getSession(String id)
{
//	System.out.println("session count="+sessions.size());
	HttpSession session= (HttpSession)sessions.get(id);
	if(session==null)
	{
		sessions.remove(id);
		
	}
	try
	{
		session.getAttributeNames();
	}catch(Exception e)
	{
		sessions.remove(id);
	}
	return session;
}
public static void removeSession(String id)
{
	//HttpSession session= (HttpSession)sessions.get(id);
	//session.invalidate();
	sessions.remove(id);
}
}