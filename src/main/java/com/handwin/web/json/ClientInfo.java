package com.handwin.web.json;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ClientInfo {
	
	private String host=null;
	private String ip=null;
	private HttpServletRequest request;
	public ClientInfo(HttpServletRequest request,
			HttpServletResponse response)
	{
		this.request=request;
		host=request.getHeader("Host");
		if(host.indexOf(':')>0)
		{
			host=host.substring(0,host.indexOf(':'));
		}
		ip=request.getHeader("x-real-ip");
		if(ip==null)
		{
			ip=request.getRemoteAddr();
		}
	}
	public String getHeader(String head)
	{
		return request.getHeader(head);
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public static void main(String[] args) {
		String host="card.cd:80";
		if(host.indexOf(':')>0)
		{
			host=host.substring(0,host.indexOf(':'));
		}		
		System.out.println(host);
	}
}
