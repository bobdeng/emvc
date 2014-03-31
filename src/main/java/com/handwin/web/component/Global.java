package com.handwin.web.component;

import java.io.IOException;
import java.io.OutputStream;

public class Global {
	 public static String PALETTE[]={"#000000","#00A8A8","#00FF00","#808040","#5454FF","#8000FF","#54FFFF","#A80000","#A8A8A8","#C8C8C8","#FF0000","#FF5454","#FF54FF","#FFFF00","#804040","#FFFFFF"};
	 
	 
	 
	 public static void print(OutputStream out,String src)
	 throws IOException
	 {
		 if(src!=null)
		 out.write(src.getBytes());
	 }
	 public static void print(OutputStream out,Integer src)
	 throws IOException
	 {
		 if(src!=null)
		 out.write(Integer.toString(src).getBytes());
	 }
	 public static void println(OutputStream out,String src)
	 throws IOException
	 {
		 out.write("\n".getBytes());
		 if(src!=null) 
			 out.write(src.getBytes());
		 out.write("\n".getBytes());
	 }
	 public static void println(OutputStream out,Integer src)
	 throws IOException
	 {
		 if(src!=null)
		 out.write(Integer.toString(src).getBytes());
		 out.write("\n".getBytes());
		 
	 }
}
