package com.handwin.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class CharsetEncode implements Filter {

	private String charset = "utf-8";

	public void destroy() {
		charset = null;
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		request.setCharacterEncoding(charset);
		chain.doFilter(request, response);
	}

	public void init(FilterConfig filterConfig) throws ServletException {
		charset = filterConfig.getInitParameter("charset");
	}

}
