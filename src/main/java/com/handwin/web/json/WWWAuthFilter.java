package com.handwin.web.json;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.handwin.web.Base64;

public class WWWAuthFilter implements Filter {

	private String serverName="";
	private WWWAuth authClass=null;
	private int tryTime=3;
	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws ServletException, IOException {
		HttpServletRequest req=(HttpServletRequest)request;
		Boolean authed=(Boolean)req.getSession().getAttribute("www_auth");
		if(authed!=null && authed)
		{
			chain.doFilter(request, response);
			return;
		}
		Integer totalTry=(Integer)req.getSession(false).getAttribute("www_auth_try");
		if(totalTry!=null && totalTry>tryTime)
		{
			return;
		}
		String authorization=req.getHeader("Authorization");
		if(authorization==null)
		{
			doAuth((HttpServletResponse)response);
			return;
		}
		String namePass=new String(Base64.decode(authorization.split("\\s+")[1]));
		if(namePass.equals(":"))
		{
			doAuth((HttpServletResponse)response);
			return;
		}
		if(authClass!=null)
		{
			String[] np=namePass.split(":");
			String name="";
			String pass="";
			if(np.length>0)
				name=np[0];
			if(np.length>1)
				pass=np[1];
			if(authClass.check(name, pass))
			{
				req.getSession().setAttribute("www_auth", new Boolean(true));
				chain.doFilter(request, response);
				return;
			}else
			{
				if(totalTry!=null)
					totalTry++;
				else
					totalTry=1;
				req.getSession().setAttribute("www_auth_try", totalTry);
				System.out.println("try :"+totalTry);
			}
		}
		
	}
	private void doAuth(HttpServletResponse response)
	{
		response.setStatus(401);
		response.setHeader("WWW-Authenticate", "Basic realm=\""+serverName+"\"");
		response.setContentType("text/html");
	}
	public static void main(String[] aerg)
	{
		System.out.println(new String(Base64.decode("YTpi")));
	}
	@Override
	public void init(FilterConfig config) throws ServletException {
		
		// TODO Auto-generated method stub
		try {
			URL url1 = new URL("file:/"
					+ config.getServletContext().getRealPath("/WEB-INF/classes")
					+ "/");
			System.out.println(url1);
			ClassLoader classLoader = new URLClassLoader(new URL[] { url1 }, this.getClass()
					.getClassLoader());
			serverName=config.getInitParameter("server");
			authClass=(WWWAuth)classLoader.loadClass(config.getInitParameter("auth_impl")).newInstance();
			String strTry=config.getInitParameter("try");
			this.tryTime=Integer.parseInt(strTry);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}

}
