package com.handwin.web;

public class UserAgentFilter {
	private String content;
	private boolean equals;
	private int clientType;
	
	public UserAgentFilter(String content, boolean equals, int clientType) {
		super();
		this.content = content.toLowerCase();
		this.equals = equals;
		this.clientType = clientType;
	}


	public boolean fit(String ua)
	{
		if(ua==null) return false;
		if(equals)
		{
			return content.equalsIgnoreCase(ua);
		}else
		{
			return ua.toLowerCase().contains(content);
		}
	}


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}


	public boolean isEquals() {
		return equals;
	}


	public void setEquals(boolean equals) {
		this.equals = equals;
	}


	public int getClientType() {
		return clientType;
	}


	public void setClientType(int clientType) {
		this.clientType = clientType;
	}
}
