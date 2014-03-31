package com.handwin.util;

import java.security.Key;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class Crypt {
	/**
	 * DES加密算法
	 * @param key 加密密钥，8个byte，不足补零，多余的取前8位
	 * @param src 要加密或者解密的内容，长度如果不是8的倍数，则在后面补齐0
	 * @param encrypt 加密还是解密。true=加密 false=解密
	 * @return 加密后的内容
	 * @throws Exception
	 */
	public static byte[] des(byte[] key, byte[] src, boolean encrypt)
			throws Exception {
		byte[] data=new byte[src.length/8*8+(src.length%8==0?0:8)];
		System.arraycopy(src, 0, data, 0, src.length);
		Cipher cipher = Cipher.getInstance("DES/ECB/NOPadding");
		byte[] desKey=new byte[8];
		System.arraycopy(key, 0, desKey, 0,8);
		DESKeySpec desKeySpec = new DESKeySpec(desKey);
		SecretKeyFactory KeyFactory = SecretKeyFactory.getInstance("DES");
		
		cipher.init(encrypt ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE,
				(Key) KeyFactory.generateSecret(desKeySpec));
		return cipher.doFinal(data);
	}
	/**
	 * 对一个Byte数组进行MD5算法
	 * @param src
	 * @return
	 * @throws Exception
	 */
	public static byte[] md5(byte[] src) throws Exception {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		return md5.digest(src);

	}	
	public static void main(String[] args) {
		try {

			byte[] rlt=des("12343236".getBytes(),"234567".getBytes(),true);
			byte[] src=des("1234所的发生的费撒56".getBytes(),rlt,false);

			System.out.println(new String(src));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
