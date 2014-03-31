package com.handwin.web;

import java.util.ArrayList;
import java.util.List;

public class Base64 {

	private static final String BASE_64_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

	public static String encode(byte[] src) {
		StringBuffer strRet = new StringBuffer();
		int i = 0;
		int j = 0;
		byte[] char_array_3 = new byte[3];
		byte[] char_array_4 = new byte[4];
		
		for (byte b : src) {
			char_array_3[i++] = b;
			if (i == 3) {
				char_array_4[0] = new Integer((char_array_3[0] & 0xfc) >> 2)
						.byteValue();
				char_array_4[1] = new Integer(((char_array_3[0] & 0x03) << 4)
						+ ((char_array_3[1] & 0xf0) >> 4)).byteValue();
				char_array_4[2] = new Integer(((char_array_3[1] & 0x0f) << 2)
						+ ((char_array_3[2] & 0xc0) >> 6)).byteValue();
				char_array_4[3] = new Integer(char_array_3[2] & 0x3f)
						.byteValue();
				for (i = 0; (i < 4); i++)
					strRet.append(BASE_64_CHARS.charAt(char_array_4[i]));
				i = 0;
			}
		}
		if (i > 0) {
			for (j = i; j < 3; j++)
				char_array_3[j] = '\0';
			char_array_4[0] = new Integer((char_array_3[0] & 0xfc) >> 2)
					.byteValue();
			char_array_4[1] = new Integer(((char_array_3[0] & 0x03) << 4)
					+ ((char_array_3[1] & 0xf0) >> 4)).byteValue();
			char_array_4[2] = new Integer(((char_array_3[1] & 0x0f) << 2)
					+ ((char_array_3[2] & 0xc0) >> 6)).byteValue();
			char_array_4[3] = new Integer(char_array_3[2] & 0x3f).byteValue();
			for (j = 0; (j < i + 1); j++) {
				strRet.append(BASE_64_CHARS.charAt(char_array_4[j]));
			}
			while ((i++ < 3)) {
				strRet.append('=');
			}
		}
		try {
			return strRet.toString();
		} catch (Exception e) {
			return null;
		}

	}

	public static byte[] decode(String src) {
		String strEncoded = null;
		try {
			strEncoded = src;//java.net.URLDecoder.decode(src, "iso-8859-1");
			if(strEncoded==null) return null;
		} catch (Exception e) {
			return null;
		}
		int in_len = strEncoded.length();
		int i = 0;
		int j = 0;
		int in_ = 0;
		List<Byte> ret = new ArrayList<Byte>();
		byte[] char_array_3 = new byte[3];
		byte[] char_array_4 = new byte[4];
		while ((in_len-- > 0) && (strEncoded.charAt(in_) != '=')) {
			char_array_4[i++] = (byte) strEncoded.charAt(in_);
			in_++;
			if (i == 4) {
				for (i = 0; i < 4; i++)
					char_array_4[i] = new Integer(BASE_64_CHARS
							.indexOf(char_array_4[i])).byteValue();
				char_array_3[0] = new Integer((char_array_4[0] << 2)
						+ ((char_array_4[1] & 0x30) >> 4)).byteValue();
				char_array_3[1] = new Integer(((char_array_4[1] & 0xf) << 4)
						+ ((char_array_4[2] & 0x3c) >> 2)).byteValue();
				char_array_3[2] = new Integer(((char_array_4[2] & 0x3) << 6)
						+ char_array_4[3]).byteValue();
				for (i = 0; (i < 3); i++)
					ret.add(char_array_3[i]);
				i = 0;
			}
		}
		if (i > 0) {
			for (j = i; j < 4; j++)
				char_array_4[j] = 0;
			for (j = 0; j < 4; j++)
				char_array_4[j] = new Integer(BASE_64_CHARS
						.indexOf(char_array_4[j])).byteValue();
			char_array_3[0] = new Integer((char_array_4[0] << 2)
					+ ((char_array_4[1] & 0x30) >> 4)).byteValue();
			char_array_3[1] = new Integer(((char_array_4[1] & 0xf) << 4)
					+ ((char_array_4[2] & 0x3c) >> 2)).byteValue();
			char_array_3[2] = new Integer(((char_array_4[2] & 0x3) << 6)
					+ char_array_4[3]).byteValue();
			for (j = 0; (j < i - 1); j++)
				ret.add(char_array_3[j]);
		}
		byte[] rlt = new byte[ret.size()];
		int k = 0;
		for (Byte b : ret) {
			rlt[k++] = b;
		}
		return rlt;

	}
	public static void main(String[] args)
	{
		System.out.println(new String(decode("AAkAaWQ9MTEmdD0wAAA%3D")));
	}
}
