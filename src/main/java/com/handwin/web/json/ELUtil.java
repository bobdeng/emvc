package com.handwin.web.json;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class ELUtil {
	public static String urlEncode(String value,String encode) throws UnsupportedEncodingException {
	    return URLEncoder.encode(value, encode);
	}
}
