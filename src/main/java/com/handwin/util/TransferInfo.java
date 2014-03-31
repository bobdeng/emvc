package com.handwin.util;

import java.util.List;

import org.apache.http.Header;
import org.apache.http.cookie.Cookie;

public class TransferInfo {
	private byte[] context;
	private int statusCode;
	private String charset;
	private boolean redirected;
	private String redirectUrl;
	private List<Cookie> cookies;
	private Header[] headers;

	public byte[] getContext() {
		return context;
	}

	public void setContext(byte[] context) {
		this.context = context;
	}

	public String getCharset() {
		return charset;
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public boolean isRedirected() {
		return redirected;
	}

	public void setRedirected(boolean redirected) {
		this.redirected = redirected;
	}

	public String getRedirectUrl() {
		return redirectUrl;
	}

	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}


	public List<Cookie> getCookies() {
		return cookies;
	}

	public void setCookies(List<Cookie> cookies) {
		this.cookies = cookies;
	}

	public Header[] getHeaders() {
		return headers;
	}

	public void setHeaders(Header[] headers) {
		this.headers = headers;
	}


}
