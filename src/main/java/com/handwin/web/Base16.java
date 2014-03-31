package com.handwin.web;

public class Base16 {

	public static String encode(byte[] src)
	{
		StringBuffer rlt=new StringBuffer();
		for (int i = 0; i < src.length; i++) {
			int v=(int) ((src[i]) & 0x000000ff);
			if(v<=0xf)
			{
				rlt.append("0");
			}
			rlt.append(Integer.toHexString(v));
		}
		return rlt.toString();
	}
	public static byte[] decode(String src)
	{
		byte[] rlt=new byte[src.length()/2];
		for(int i=0;i<src.length();i+=2)
		{
			rlt[i/2]=(byte)Integer.parseInt(src.substring(i,i+2),16);
		}
		return rlt;
	}	
	public static void main(String[] args) throws Exception{
		String s="邓志国hello";
		String encode=encode(s.getBytes("utf-8"));
		System.out.println(encode);
		System.out.println(new String(decode(encode),"utf-8"));
	}
}
