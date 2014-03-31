package com.handwin.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecFactory;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

public class TransferUtil {
	public static final int DEFAULT_TIMEOUT = 10000;

	public static final int HTTP_ERR = -1;

	public static final int IO_ERR = -2;

	public static final int LOCATION_ERR = -3;

	private static Logger log = Logger.getLogger(TransferUtil.class);

	private static int timeout = DEFAULT_TIMEOUT;
	
	public static int getTimeout() {
		return timeout;
	}
	static CookieSpecFactory csf = new CookieSpecFactory(){

        public CookieSpec newInstance(HttpParams params){

            return new BrowserCompatSpec(){

                @Override

                public void validate(Cookie cookie, CookieOrigin origin)

                throws MalformedCookieException{

                    //Oh, I am easy

                }

            };

        }

    };
    
	public static void setTimeout(int timeout) {
		TransferUtil.timeout = timeout;
	}

	public TransferUtil() {
		// timeout = DEFAULT_TIMEOUT;
	}

	public TransferUtil(int timeout) {
		timeout = DEFAULT_TIMEOUT;
	}

	/**
	 * 
	 * @param url
	 * @param sessionName
	 * @param sessionID
	 * @return
	 */
	public static TransferInfo getMethod(String url, List<Cookie> cookies,
			String referer) {
		TransferInfo result = new TransferInfo();
		result.setRedirected(false);
		HttpClient httpClient = new DefaultHttpClient();
		try {
			httpClient.getParams().setIntParameter("http.socket.timeout",
					timeout);
			httpClient.getParams().setParameter("http.connection.stalecheck",
					new Boolean(false));

			// cookie
			if (null != cookies && cookies.size() > 0) {
				httpClient.getParams().setParameter(
						"http.protocol.single-cookie-header", true);
				httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY,
						CookiePolicy.BROWSER_COMPATIBILITY);
				CookieStore cookieStore = new BasicCookieStore();
				for (int i = 0; i < cookies.size(); i++) {
					cookieStore.addCookie(cookies.get(i));
				}
				((AbstractHttpClient) httpClient).setCookieStore(cookieStore);
			}

			HttpGet httpGet = new HttpGet(url);
			((AbstractHttpClient) httpClient)
					.setHttpRequestRetryHandler(new MyHttpRequestRetryHandler());
			httpGet.addHeader("Accept-Encoding", "gzip");
			// httpGet.addHeader("Content-Type", "text/html; charset=UTF-8");
			// httpGet.addHeader("Connection", "keep-alive");
			if (null != referer) {
				httpGet.setHeader("referer", referer);
			}
			HttpResponse response = httpClient.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY
					|| statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
				Header locationHeader = response.getFirstHeader("location");
				String location = null;
				result.setRedirected(true);
				if (locationHeader != null) {
					location = locationHeader.getValue();
					log.warn("The page was redirected to:" + location);
					result.setRedirectUrl(location);
				} else {
					System.out.println("Location field value is null.");
					result.setStatusCode(LOCATION_ERR);
				}
			}
			result.setHeaders(response.getAllHeaders());
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				Header[] codeHeaders = response.getHeaders("content-encoding");
				boolean isZip = false;
				if (codeHeaders != null && codeHeaders.length > 0) {
					for (int i = 0; i < codeHeaders.length; i++) {
						if ("gzip".equalsIgnoreCase(codeHeaders[i].getValue())) {
							isZip = true;
							break;
						}
					}
				}
				if (isZip) {
					result.setContext(unzip(EntityUtils.toByteArray(entity)));
				} else {
					result.setContext(EntityUtils.toByteArray(entity));
				}
			}
			String charset = EntityUtils.getContentCharSet(entity);
			if (charset == null) {
				charset = HTTP.DEFAULT_CONTENT_CHARSET;
			}
			result.setCharset(charset);
			result.setCookies(((AbstractHttpClient) httpClient)
					.getCookieStore().getCookies());
			result.setStatusCode(statusCode);
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			//log.error(e);
			//e.printStackTrace();
			result.setStatusCode(HTTP_ERR);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			//log.error(e);
			e.printStackTrace();
			result.setStatusCode(IO_ERR);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return result;
	}

	/**
	 * 
	 * @param url
	 * @param sessionName
	 * @param sessionID
	 * @return
	 */
	public static TransferInfo getMethod(String url, List<Cookie> cookies,
			String referer, int timeout) {
		TransferInfo result = new TransferInfo();
		result.setRedirected(false);
		HttpClient httpClient = new DefaultHttpClient();
		try {
			httpClient.getParams().setIntParameter("http.socket.timeout",
					timeout);
			httpClient.getParams().setParameter("http.connection.stalecheck",
					new Boolean(false));

			// cookie
			if (null != cookies && cookies.size() > 0) {
				httpClient.getParams().setParameter(
						"http.protocol.single-cookie-header", true);
				httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY,
						CookiePolicy.BROWSER_COMPATIBILITY);
				CookieStore cookieStore = new BasicCookieStore();
				for (int i = 0; i < cookies.size(); i++) {
					cookieStore.addCookie(cookies.get(i));
				}
				((AbstractHttpClient) httpClient).setCookieStore(cookieStore);
			}

			HttpGet httpGet = new HttpGet(url);
			((AbstractHttpClient) httpClient)
					.setHttpRequestRetryHandler(new MyHttpRequestRetryHandler());
			httpGet.addHeader("Accept-Encoding", "gzip");
			// httpGet.addHeader("Content-Type", "text/html; charset=UTF-8");
			// httpGet.addHeader("Connection", "keep-alive");
			if (null != referer) {
				httpGet.setHeader("referer", referer);
			}
			HttpResponse response = httpClient.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY
					|| statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
				Header locationHeader = response.getFirstHeader("location");
				String location = null;
				result.setRedirected(true);
				if (locationHeader != null) {
					location = locationHeader.getValue();
					log.warn("The page was redirected to:" + location);
					result.setRedirectUrl(location);
				} else {
					System.out.println("Location field value is null.");
					result.setStatusCode(LOCATION_ERR);
				}
			}
			result.setHeaders(response.getAllHeaders());
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				Header[] codeHeaders = response.getHeaders("content-encoding");
				boolean isZip = false;
				if (codeHeaders != null && codeHeaders.length > 0) {
					for (int i = 0; i < codeHeaders.length; i++) {
						if ("gzip".equalsIgnoreCase(codeHeaders[i].getValue())) {
							isZip = true;
							break;
						}
					}
				}
				if (isZip) {
					result.setContext(unzip(EntityUtils.toByteArray(entity)));
				} else {
					result.setContext(EntityUtils.toByteArray(entity));
				}
			}
			String charset = EntityUtils.getContentCharSet(entity);
			if (charset == null) {
				charset = HTTP.DEFAULT_CONTENT_CHARSET;
			}
			result.setCharset(charset);
			result.setCookies(((AbstractHttpClient) httpClient)
					.getCookieStore().getCookies());
			result.setStatusCode(statusCode);
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			log.error(e);
			e.printStackTrace();
			result.setStatusCode(HTTP_ERR);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e);
			e.printStackTrace();
			result.setStatusCode(IO_ERR);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return result;
	}

	public static TransferInfo fileUploadMethod(String url,
			List<Cookie> cookies, List<NameValuePair> data, String referer,
			List<UploadFile> files) {
		TransferInfo result = new TransferInfo();
		HttpClient httpClient = new DefaultHttpClient();
		try {
			httpClient.getParams().setIntParameter("http.socket.timeout",
					timeout);
			httpClient.getParams().setParameter("http.connection.stalecheck",
					new Boolean(false));
			HttpPost httpPost = new HttpPost(url);
			((AbstractHttpClient) httpClient)
					.setHttpRequestRetryHandler(new MyHttpRequestRetryHandler());
			httpPost.addHeader("Accept-Encoding", "gzip");
			// httpPost.addHeader("Content-Type", "text/html; charset=UTF-8");
			MultipartEntity reqEntity = new MultipartEntity();
			if (data != null && data.size() > 0) {
				for (int i = 0; i < data.size(); i++) {
					NameValuePair d = data.get(i);
					reqEntity.addPart(d.getName(), new StringBody(d.getValue(),
							Charset.forName("utf-8")));
				}
			}
			if (files != null && files.size() > 0) {
				for (int i = 0; i < files.size(); i++) {
					UploadFile file = files.get(i);
					reqEntity.addPart(file.getFiledName(), new ByteArrayBody(
							file.getData(), file.getMimeType(),file.getFileName()));
				}
			}
			// UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(data,
			// "utf-8");
			httpPost.setEntity(reqEntity);
			// cookie
			if (null != cookies && cookies.size() > 0) {
				httpClient.getParams().setParameter(
						"http.protocol.single-cookie-header", true);
				httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY,
						CookiePolicy.BROWSER_COMPATIBILITY);
				CookieStore cookieStore = new BasicCookieStore();
				for (int i = 0; i < cookies.size(); i++) {
					cookieStore.addCookie(cookies.get(i));
				}
				((AbstractHttpClient) httpClient).setCookieStore(cookieStore);
			}
			if (referer != null)
				httpPost.setHeader("referer", referer);
			// 鎵ц
			HttpResponse response = httpClient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY
					|| statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
				Header locationHeader = response.getFirstHeader("location");
				String location = null;
				result.setRedirected(true);
				if (locationHeader != null) {
					location = locationHeader.getValue();
					log.warn("The page was redirected to:" + location);
					result.setRedirectUrl(location);
				} else {
					System.out.println("Location field value is null.");
					result.setStatusCode(LOCATION_ERR);
				}
			}
			result.setHeaders(response.getAllHeaders());
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				Header[] codeHeaders = response.getHeaders("content-encoding");
				boolean isZip = false;
				if (codeHeaders != null && codeHeaders.length > 0) {
					for (int i = 0; i < codeHeaders.length; i++) {
						if ("gzip".equalsIgnoreCase(codeHeaders[i].getValue())) {
							isZip = true;
							break;
						}
					}
				}
				if (isZip) {
					result.setContext(unzip(EntityUtils.toByteArray(entity)));
				} else {
					result.setContext(EntityUtils.toByteArray(entity));
				}
			}
			String charset = EntityUtils.getContentCharSet(entity);
			if (charset == null) {
				charset = HTTP.DEFAULT_CONTENT_CHARSET;
			}
			result.setCharset(charset);
			result.setCookies(((AbstractHttpClient) httpClient)
					.getCookieStore().getCookies());
			result.setStatusCode(statusCode);
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			log.error(e);
			e.printStackTrace();
			result.setStatusCode(HTTP_ERR);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e);
			e.printStackTrace();
			result.setStatusCode(IO_ERR);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return result;
	}
	public static TransferInfo fileUploadMethodSSL(String url,
			List<Cookie> cookies, List<NameValuePair> data, String referer,
			List<UploadFile> files) {
		TransferInfo result = new TransferInfo();
		HttpClient httpClient = null;
		try {
			SSLContext sslContext = SSLContext.getInstance("SSL");
			// set up a TrustManager that trusts everything
			sslContext.init(null,
					new TrustManager[] { new EasyX509TrustManager(null) },
					new SecureRandom());
			SSLSocketFactory sf = new SSLSocketFactory(sslContext);
			Scheme httpsScheme = new Scheme("https", 443, sf);
			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(httpsScheme);

			ClientConnectionManager cm = new SingleClientConnManager(
					schemeRegistry);
			httpClient = new DefaultHttpClient(cm);
			httpClient.getParams().setIntParameter("http.socket.timeout",
					timeout);
			httpClient.getParams().setParameter("http.connection.stalecheck",
					new Boolean(false));
			HttpPost httpPost = new HttpPost(url);
			((AbstractHttpClient) httpClient)
					.setHttpRequestRetryHandler(new MyHttpRequestRetryHandler());
			httpPost.addHeader("Accept-Encoding", "gzip");
			// httpPost.addHeader("Content-Type", "text/html; charset=UTF-8");
			MultipartEntity reqEntity = new MultipartEntity();
			if (data != null && data.size() > 0) {
				for (int i = 0; i < data.size(); i++) {
					NameValuePair d = data.get(i);
					reqEntity.addPart(d.getName(), new StringBody(d.getValue(),
							Charset.forName("utf-8")));
				}
			}
			if (files != null && files.size() > 0) {
				for (int i = 0; i < files.size(); i++) {
					UploadFile file = files.get(i);
					reqEntity.addPart(file.getFiledName(), new ByteArrayBody(
							file.getData(),file.getMimeType(), file.getFileName()));
				}
			}
			// UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(data,
			// "utf-8");
			httpPost.setEntity(reqEntity);
			// cookie
			if (null != cookies && cookies.size() > 0) {
				httpClient.getParams().setParameter(
						"http.protocol.single-cookie-header", true);
				httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY,
						CookiePolicy.BROWSER_COMPATIBILITY);
				CookieStore cookieStore = new BasicCookieStore();
				for (int i = 0; i < cookies.size(); i++) {
					cookieStore.addCookie(cookies.get(i));
				}
				((AbstractHttpClient) httpClient).setCookieStore(cookieStore);
			}
			if (referer != null)
				httpPost.setHeader("referer", referer);
			// 鎵ц
			HttpResponse response = httpClient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY
					|| statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
				Header locationHeader = response.getFirstHeader("location");
				String location = null;
				result.setRedirected(true);
				if (locationHeader != null) {
					location = locationHeader.getValue();
					log.warn("The page was redirected to:" + location);
					result.setRedirectUrl(location);
				} else {
					System.out.println("Location field value is null.");
					result.setStatusCode(LOCATION_ERR);
				}
			}
			result.setHeaders(response.getAllHeaders());
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				Header[] codeHeaders = response.getHeaders("content-encoding");
				boolean isZip = false;
				if (codeHeaders != null && codeHeaders.length > 0) {
					for (int i = 0; i < codeHeaders.length; i++) {
						if ("gzip".equalsIgnoreCase(codeHeaders[i].getValue())) {
							isZip = true;
							break;
						}
					}
				}
				if (isZip) {
					result.setContext(unzip(EntityUtils.toByteArray(entity)));
				} else {
					result.setContext(EntityUtils.toByteArray(entity));
				}
			}
			String charset = EntityUtils.getContentCharSet(entity);
			if (charset == null) {
				charset = HTTP.DEFAULT_CONTENT_CHARSET;
			}
			result.setCharset(charset);
			result.setCookies(((AbstractHttpClient) httpClient)
					.getCookieStore().getCookies());
			result.setStatusCode(statusCode);
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			log.error(e);
			e.printStackTrace();
			result.setStatusCode(HTTP_ERR);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e);
			e.printStackTrace();
			result.setStatusCode(IO_ERR);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return result;
	}
	private static byte[] unzip(byte[] data) throws Exception {
		ByteArrayInputStream input = new ByteArrayInputStream(data);
		GZIPInputStream gzip = new GZIPInputStream(input);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buf = new byte[1024];
		int num = 0;
		while ((num = gzip.read(buf)) != -1) {
			out.write(buf, 0, num);
		}
		return out.toByteArray();
	}

	/**
	 * 
	 * @param url
	 * @param sessionName
	 * @param sessionID
	 * @param data
	 * @return
	 */
	public static TransferInfo postMethod(String url, List<Cookie> cookies,
			List<NameValuePair> data, String referer) {
		TransferInfo result = new TransferInfo();
		HttpClient httpClient = new DefaultHttpClient();
		((AbstractHttpClient) httpClient).getCookieSpecs().register("easy", csf);

        httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY, "easy");
		try {
			httpClient.getParams().setIntParameter("http.socket.timeout",
					timeout);
			httpClient.getParams().setParameter("http.connection.stalecheck",
					new Boolean(false));
			HttpPost httpPost = new HttpPost(url);
			((AbstractHttpClient) httpClient)
					.setHttpRequestRetryHandler(new MyHttpRequestRetryHandler());
			httpPost.addHeader("Accept-Encoding", "gzip");
			httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
			if (data != null) {
				UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(
						data, "gbk");
				httpPost.setEntity(postEntity);
			}
			// cookie
			if (null != cookies && cookies.size() > 0) {
//				httpClient.getParams().setParameter(
//						"http.protocol.single-cookie-header", true);
//				httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY,
//						CookiePolicy.BROWSER_COMPATIBILITY);
//				//CookieStore cookieStore = new BasicCookieStore();
//				for (int i = 0; i < cookies.size(); i++) {
//					((AbstractHttpClient) httpClient).getCookieStore().addCookie(cookies.get(i));
//					//cookieStore.addCookie(cookies.get(i));
//				}
				StringBuffer header=new StringBuffer();
				for (int i = 0; i < cookies.size(); i++) 
					header.append(cookies.get(i).getName()+"="+cookies.get(i).getValue()+"; ");
				httpPost.addHeader("Cookie",header.toString());
				//((AbstractHttpClient) httpClient).setCookieStore(cookieStore);
			}
			System.out.println(((AbstractHttpClient) httpClient).getCookieStore().getCookies().size());
			if (referer != null)
				httpPost.setHeader("referer", referer);
			// 鎵ц
			HttpResponse response = httpClient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY
					|| statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
				Header locationHeader = response.getFirstHeader("location");
				String location = null;
				result.setRedirected(true);
				if (locationHeader != null) {
					location = locationHeader.getValue();
					log.warn("The page was redirected to:" + location);
					result.setRedirectUrl(location);
				} else {
					System.out.println("Location field value is null.");
					result.setStatusCode(LOCATION_ERR);
				}
			}
			result.setHeaders(response.getAllHeaders());
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				Header[] codeHeaders = response.getHeaders("content-encoding");
				boolean isZip = false;
				if (codeHeaders != null && codeHeaders.length > 0) {
					for (int i = 0; i < codeHeaders.length; i++) {
						if ("gzip".equalsIgnoreCase(codeHeaders[i].getValue())) {
							isZip = true;
							break;
						}
					}
				}
				if (isZip) {
					result.setContext(unzip(EntityUtils.toByteArray(entity)));
				} else {
					result.setContext(EntityUtils.toByteArray(entity));
				}
			}
			String charset = EntityUtils.getContentCharSet(entity);
			if (charset == null) {
				charset = HTTP.DEFAULT_CONTENT_CHARSET;
			}
			result.setCharset(charset);
			result.setCookies(((AbstractHttpClient) httpClient)
					.getCookieStore().getCookies());
			result.setStatusCode(statusCode);
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			log.error(e);
			e.printStackTrace();
			result.setStatusCode(HTTP_ERR);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e);
			e.printStackTrace();
			result.setStatusCode(IO_ERR);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return result;
	}

	public static TransferInfo postMethod(String url, List<Cookie> cookies,
			String data, String referer) throws Exception {
		TransferInfo result = new TransferInfo();
		HttpClient httpClient = new DefaultHttpClient();
		try {
			httpClient.getParams().setIntParameter("http.socket.timeout",
					timeout);
			httpClient.getParams().setParameter("http.connection.stalecheck",
					new Boolean(false));
			HttpPost httpPost = new HttpPost(url);
			((AbstractHttpClient) httpClient)
					.setHttpRequestRetryHandler(new MyHttpRequestRetryHandler());
			httpPost.addHeader("Accept-Encoding", "gzip");
			// httpPost.addHeader("Content-Type", "text/html; charset=UTF-8");
			if (data != null) {
				StringEntity postEntity = new StringEntity(data,
						"application/x-www-form-urlencoded", "utf-8");
				httpPost.setEntity(postEntity);
			}

			// cookie
			if (null != cookies && cookies.size() > 0) {
				httpClient.getParams().setParameter(
						"http.protocol.single-cookie-header", true);
				httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY,
						CookiePolicy.BROWSER_COMPATIBILITY);
				CookieStore cookieStore = new BasicCookieStore();
				for (int i = 0; i < cookies.size(); i++) {
					cookieStore.addCookie(cookies.get(i));
				}
				((AbstractHttpClient) httpClient).setCookieStore(cookieStore);
			}
			if (referer != null)
				httpPost.setHeader("referer", referer);
			// 鎵ц
			HttpResponse response = httpClient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY
					|| statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
				Header locationHeader = response.getFirstHeader("location");
				String location = null;
				result.setRedirected(true);
				if (locationHeader != null) {
					location = locationHeader.getValue();
					log.warn("The page was redirected to:" + location);
					result.setRedirectUrl(location);
				} else {
					System.out.println("Location field value is null.");
					result.setStatusCode(LOCATION_ERR);
				}
			}
			result.setHeaders(response.getAllHeaders());
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				Header[] codeHeaders = response.getHeaders("content-encoding");
				boolean isZip = false;
				if (codeHeaders != null && codeHeaders.length > 0) {
					for (int i = 0; i < codeHeaders.length; i++) {
						if ("gzip".equalsIgnoreCase(codeHeaders[i].getValue())) {
							isZip = true;
							break;
						}
					}
				}
				if (isZip) {
					result.setContext(unzip(EntityUtils.toByteArray(entity)));
				} else {
					result.setContext(EntityUtils.toByteArray(entity));
				}
			}
			String charset = EntityUtils.getContentCharSet(entity);
			if (charset == null) {
				charset = HTTP.DEFAULT_CONTENT_CHARSET;
			}
			result.setCharset(charset);
			result.setCookies(((AbstractHttpClient) httpClient)
					.getCookieStore().getCookies());
			result.setStatusCode(statusCode);
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			log.error(e);
			e.printStackTrace();
			result.setStatusCode(HTTP_ERR);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e);
			e.printStackTrace();
			result.setStatusCode(IO_ERR);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return result;
	}

	public static TransferInfo postMethodSSL(String url, List<Cookie> cookies,
			List<NameValuePair> data, String referer) {
		TransferInfo result = new TransferInfo();
		result.setRedirected(false);
		HttpClient httpClient = null;
		try {
			// 璁剧疆SSL
			SSLContext sslContext = SSLContext.getInstance("SSL");
			// set up a TrustManager that trusts everything
			sslContext.init(null,
					new TrustManager[] { new EasyX509TrustManager(null) },
					new SecureRandom());
			SSLSocketFactory sf = new SSLSocketFactory(sslContext);
			Scheme httpsScheme = new Scheme("https", 443, sf);
			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(httpsScheme);

			ClientConnectionManager cm = new SingleClientConnManager(
					schemeRegistry);
			httpClient = new DefaultHttpClient(cm);
			httpClient.getParams().setIntParameter("http.socket.timeout",
					timeout);
			httpClient.getParams().setParameter("http.connection.stalecheck",
					new Boolean(false));
			HttpPost httpPost = new HttpPost(url);
			((AbstractHttpClient) httpClient)
					.setHttpRequestRetryHandler(new MyHttpRequestRetryHandler());
			httpPost.addHeader("Accept-Encoding", "gzip");
			// httpPost.addHeader("Content-Type", "text/html; charset=UTF-8");
			if (data != null) {
				UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(
						data, "utf-8");
				httpPost.setEntity(postEntity);
			}
			// cookie
			if (null != cookies && cookies.size() > 0) {
				httpClient.getParams().setParameter(
						"http.protocol.single-cookie-header", true);
				httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY,
						CookiePolicy.BROWSER_COMPATIBILITY);
				CookieStore cookieStore = new BasicCookieStore();
				for (int i = 0; i < cookies.size(); i++) {
					cookieStore.addCookie(cookies.get(i));
				}
				((AbstractHttpClient) httpClient).setCookieStore(cookieStore);
			}
			if (referer != null)
				httpPost.setHeader("referer", referer);
			// 鎵ц
			HttpResponse response = httpClient.execute(httpPost);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY
					|| statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
				Header locationHeader = response.getFirstHeader("location");
				String location = null;
				result.setRedirected(true);
				if (locationHeader != null) {
					location = locationHeader.getValue();
					log.warn("The page was redirected to:" + location);
					result.setRedirectUrl(location);
				} else {
					System.out.println("Location field value is null.");
					result.setStatusCode(LOCATION_ERR);
				}
			}
			result.setHeaders(response.getAllHeaders());
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				Header[] codeHeaders = response.getHeaders("content-encoding");
				boolean isZip = false;
				if (codeHeaders != null && codeHeaders.length > 0) {
					for (int i = 0; i < codeHeaders.length; i++) {
						if ("gzip".equalsIgnoreCase(codeHeaders[i].getValue())) {
							isZip = true;
							break;
						}
					}
				}
				if (isZip) {
					result.setContext(unzip(EntityUtils.toByteArray(entity)));
				} else {
					result.setContext(EntityUtils.toByteArray(entity));
				}
			}
			String charset = EntityUtils.getContentCharSet(entity);
			if (charset == null) {
				charset = HTTP.DEFAULT_CONTENT_CHARSET;
			}
			result.setCharset(charset);
			result.setCookies(((AbstractHttpClient) httpClient)
					.getCookieStore().getCookies());
			result.setStatusCode(statusCode);
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			log.error(e);
			e.printStackTrace();
			result.setStatusCode(HTTP_ERR);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e);
			e.printStackTrace();
			result.setStatusCode(IO_ERR);
		} finally {
			if (httpClient != null)
				httpClient.getConnectionManager().shutdown();
		}
		return result;
	}

	public static TransferInfo getMethodSSL(String url, List<Cookie> cookies,
			String referer) {
		TransferInfo result = new TransferInfo();
		result.setRedirected(false);
		HttpClient httpClient = null;
		try {
			// 璁剧疆SSL
			SSLContext sslContext = SSLContext.getInstance("SSL");
			// set up a TrustManager that trusts everything
			sslContext.init(null,
					new TrustManager[] { new EasyX509TrustManager(null) },
					new SecureRandom());
			SSLSocketFactory sf = new SSLSocketFactory(sslContext);
			Scheme httpsScheme = new Scheme("https", 443, sf);
			SchemeRegistry schemeRegistry = new SchemeRegistry();
			schemeRegistry.register(httpsScheme);

			ClientConnectionManager cm = new SingleClientConnManager(
					schemeRegistry);
			httpClient = new DefaultHttpClient(cm);
			httpClient.getParams().setIntParameter("http.socket.timeout",
					timeout);
			httpClient.getParams().setParameter("http.connection.stalecheck",
					new Boolean(false));

			// cookie
			if (null != cookies && cookies.size() > 0) {
				httpClient.getParams().setParameter(
						"http.protocol.single-cookie-header", true);
				httpClient.getParams().setParameter(ClientPNames.COOKIE_POLICY,
						CookiePolicy.BROWSER_COMPATIBILITY);
				CookieStore cookieStore = new BasicCookieStore();
				for (int i = 0; i < cookies.size(); i++) {
					cookieStore.addCookie(cookies.get(i));
				}
				((AbstractHttpClient) httpClient).setCookieStore(cookieStore);
			}

			HttpGet httpGet = new HttpGet(url);
			((AbstractHttpClient) httpClient)
					.setHttpRequestRetryHandler(new MyHttpRequestRetryHandler());
			httpGet.addHeader("Accept-Encoding", "gzip");
			// httpGet.addHeader("Content-Type", "text/html; charset=UTF-8");
			// httpGet.addHeader("Connection", "keep-alive");
			if (null != referer) {
				httpGet.setHeader("referer", referer);
			}
			HttpResponse response = httpClient.execute(httpGet);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY
					|| statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
				Header locationHeader = response.getFirstHeader("location");
				String location = null;
				result.setRedirected(true);
				if (locationHeader != null) {
					location = locationHeader.getValue();
					log.warn("The page was redirected to:" + location);
					result.setRedirectUrl(location);
				} else {
					System.out.println("Location field value is null.");
					result.setStatusCode(LOCATION_ERR);
				}
			}
			result.setHeaders(response.getAllHeaders());
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				Header[] codeHeaders = response.getHeaders("content-encoding");
				boolean isZip = false;
				if (codeHeaders != null && codeHeaders.length > 0) {
					for (int i = 0; i < codeHeaders.length; i++) {
						if ("gzip".equalsIgnoreCase(codeHeaders[i].getValue())) {
							isZip = true;
							break;
						}
					}
				}
				if (isZip) {
					result.setContext(unzip(EntityUtils.toByteArray(entity)));
				} else {
					result.setContext(EntityUtils.toByteArray(entity));
				}
			}
			String charset = EntityUtils.getContentCharSet(entity);
			if (charset == null) {
				charset = HTTP.DEFAULT_CONTENT_CHARSET;
			}
			result.setCharset(charset);
			result.setCookies(((AbstractHttpClient) httpClient)
					.getCookieStore().getCookies());
			result.setStatusCode(statusCode);
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			log.error(e);
			e.printStackTrace();
			result.setStatusCode(HTTP_ERR);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e);
			e.printStackTrace();
			result.setStatusCode(IO_ERR);
		} finally {
			if (httpClient != null)
				httpClient.getConnectionManager().shutdown();
		}
		return result;
	}

	//
	public static Cookie generateCookie(String sessionID, String sessionName,
			String fullURL) {
		BasicClientCookie cookie = new BasicClientCookie(sessionName, sessionID);
		cookie.setDomain(parseCookieDomainName(fullURL));
		cookie.setPath("/");
		return cookie;
	}

	//
	private static String parseCookieDomainName(String url) {
		if (url == null)
			return null;
		String domainName = null;
		String aUrl = url.replaceAll(" ", "");
		if (!aUrl.startsWith("http")) {
			return null;
		}
		int beginPos = 7;
		if (aUrl.startsWith("https://")) {
			beginPos = 8;
		}
		aUrl = aUrl.substring(beginPos, aUrl.length());
		while (aUrl.endsWith("/")) {
			aUrl = aUrl.substring(0, aUrl.length() - 1);
		}
		int firstSlashPos = aUrl.indexOf("/");
		String[] urlSegs = null;
		if (firstSlashPos > 0) {
			urlSegs = aUrl.split("/");
		} else {
			urlSegs = new String[1];
			urlSegs[0] = aUrl;
		}
		if (urlSegs[0].indexOf(":") > 0) {
			domainName = urlSegs[0].split(":")[0];
		} else {
			domainName = urlSegs[0];
		}
		return domainName;
	}

	public static void main(String[] args) throws Exception{
		// TODO Auto-generated method stub
		List<Cookie> cookies=new ArrayList<Cookie>();
		cookies.add(new MyCookie("pprdig", "rdU_2brJLjGC4j5RpgnJX5j_kmJbhYV6-3Fq2mYNYth6NgoL9X3QCKlxlEtQ6PxNoEs51aKedJnk_I0qIHYh35slcTVwWJZSKIWxjgteNg_TCsEUJwXAFzz8YiGEgV2WXjldeaeUKLhXKk9sOnR2rbX47ISEh_T4xYKj2mlJ7aQ"));
		cookies.add(new MyCookie("ppinf", "2|1350396122|1351605722|bG9naW5pZDowOnx1c2VyaWQ6MTU6Ym9iZGVuZ0AxNjMuY29tfHNlcnZpY2V1c2U6MjA6MDAwMDAwMDAwMDAwMDAwMDAwMDB8Y3J0OjEwOjIwMTItMDgtMDd8ZW10OjE6MHxhcHBpZDo0OjEwNzN8dHJ1c3Q6MToxfHBhcnRuZXJpZDoxOjB8cmVsYXRpb246MDp8dXVpZDoxNjoyN2FkNzY4NzYxZTg0NmFvfHVpZDo5OmUzNjM2MjM1OXx1bmlxbmFtZTo1NDolRTklODIlOTMlRTUlQkYlOTclRTUlOUIlQkQlRTclOUElODQlRTUlQkUlQUUlRTUlOEQlOUF8"));
		List<NameValuePair> data=new ArrayList<NameValuePair>();
		data.add(new NameValuePair() {
			
			@Override
			public String getValue() {
				// TODO Auto-generated method stub
				return "鏈井鍗氱敱鏈哄櫒浜虹敤Cookie鍙戦�锛屾祻瑙堝櫒宸茬粡閫�嚭鐧诲綍锛孈閭撳織鍥界殑寰崥 @liuwater";
			}
			
			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return "msg";
			}
		});
		TransferInfo info=TransferUtil.postMethod("http://t.sohu.com/twAction/insertTwitter", cookies, data, "http://t.sohu.com/home");
		System.out.println(info.getStatusCode());
		System.out.println(new String(info.getContext(),info.getCharset()));
	}
	

}
