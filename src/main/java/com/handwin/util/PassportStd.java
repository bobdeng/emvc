package com.handwin.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.security.Key;
import java.security.Security;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class PassportStd {
	static
	{
		Security.addProvider(new BouncyCastleProvider()); 
	}
	public static byte[] desede(byte[] key, byte[] src, boolean encrypt)
	throws Exception {
		Cipher cipher = Cipher.getInstance("DESede/ECB/NOPadding");
		DESedeKeySpec desKey = new DESedeKeySpec(key);
		SecretKeyFactory KeyFactory = SecretKeyFactory.getInstance("DESede");
		
		cipher.init(encrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE,
				(Key) KeyFactory.generateSecret(desKey));
		return cipher.doFinal(src);
	}
	//public static byte[] key="012345678901234567891234".getBytes();
	public static <T> T fromString(byte[] key,String str,Class<T> clz)throws Exception
	{
		T rlt=clz.newInstance();
		Field[] fields=clz.getDeclaredFields();
		ByteArrayInputStream input=new ByteArrayInputStream(desede(key,Base64.decode(str),false));
		for(Field f:fields)
		{
			f.setAccessible(true);
			switch(f.getType().getName().hashCode())
			{
				case 1195259493://string
					f.set(rlt, new String(BytesUtils.readByteArray(input),"utf-8"));
					break;
				case 104431://int
					f.set(rlt, BytesUtils.ReadInt(input, 4));
					break;
				case 3327612://long
					f.set(rlt, BytesUtils.readLong(input, 8));
					break;
			}
		}
		return rlt;
	}
	public static String toString(byte[] key,Object ppt)throws Exception
	{
		Field[] fields=ppt.getClass().getDeclaredFields();
		ByteArrayOutputStream out=new ByteArrayOutputStream();
		for(Field f:fields)
		{
			f.setAccessible(true);
			Object v=f.get(ppt);
			switch(f.getType().getName().hashCode())
			{
				case 1195259493://string
					if(v==null)
						BytesUtils.writeByteArray(out, null);
					else
						BytesUtils.writeByteArray(out,((String)v).getBytes("utf-8"));
					break;
				case 104431://int
					BytesUtils.writeInt(out, (Integer)v);
					break;
				case 3327612://long
					BytesUtils.writeLong(out, (Long)v);
					break;
			}
		}
		Random random=new Random();
		while(out.size()%8!=0)
			out.write(random.nextInt(255));
		return Base64.encode(desede(key,out.toByteArray(),true));
	}
	public static void main(String[] args) throws Exception{
		
}
	
}
