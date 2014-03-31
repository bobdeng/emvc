package com.handwin.web;

import java.util.ArrayList;
import java.util.List;




public class UserAgent {

	public static final int TYPE_WEB=0;
	public static final int TYPE_WAP1=1;
	public static final int TYPE_WAP2=2;
	public static final int TYPE_PC=3;
	public static final int TYPE_WEBKIT=4;
	
	public static boolean canSendSms(String ua)
	{
		if(ua.contains("symbianos")) return true;
		if(ua.contains("miui")) return true;
		return false;
	}
	private static final List<UserAgentFilter> filters=new ArrayList<UserAgentFilter>();
	static
	{	
		filters.add(new UserAgentFilter("ucweb",false,TYPE_WAP1));
		filters.add(new UserAgentFilter("nokia63",false,TYPE_WAP1));
		filters.add(new UserAgentFilter("chrome",false,TYPE_PC));
		filters.add(new UserAgentFilter("meizu",false,TYPE_WEBKIT));
		filters.add(new UserAgentFilter("iphone",false,TYPE_WEBKIT));
		filters.add(new UserAgentFilter("symbian",false,TYPE_WAP2));
		filters.add(new UserAgentFilter("symbianos/9.4",false,TYPE_WEBKIT));
		filters.add(new UserAgentFilter("symbianos/9.3",false,TYPE_WEBKIT));
		filters.add(new UserAgentFilter("symbianos/9.2",false,TYPE_WEBKIT));
		filters.add(new UserAgentFilter("windows nt",false,TYPE_PC));
		filters.add(new UserAgentFilter("webkit",false,TYPE_WEBKIT));
		filters.add(new UserAgentFilter("android",false,TYPE_WEBKIT));
		filters.add(new UserAgentFilter("windows phone",false,TYPE_WEBKIT));
		filters.add(new UserAgentFilter("windows ce",false,TYPE_WEBKIT));
		filters.add(new UserAgentFilter("symbianos",false,TYPE_WAP2));
		filters.add(new UserAgentFilter("wap2.0",false,TYPE_WAP2));
		filters.add(new UserAgentFilter("profile/midp",false,TYPE_WAP1));	
		filters.add(new UserAgentFilter("opera",false,TYPE_WEB));
		filters.add(new UserAgentFilter("firefox",false,TYPE_PC));
	}
	public static int getClientType(String ua)
	{
		System.out.println(ua);
		if(ua==null)
			return 	TYPE_WAP1;
		for(int i=0;i<filters.size();i++)
		{
			System.out.println(filters.get(i).getContent());
			if(filters.get(i).fit(ua))
			{
				return filters.get(i).getClientType();
			}
		}
		return TYPE_WAP1;
	}
	public static void main(String[] args)
	{
		System.out.println(getClientType("ddsa"));
		//System.out.println(getClientType("Mozilla/5.0 (Linux; U; Android 1.6; zh-cn; HTC_TATTOO_A3288 Build/DRC79) AppleWebKit/528.5+ (KHTML, like Gecko) Version/3.1.2 Mobile Safari/525.20.1"));
	}
}

