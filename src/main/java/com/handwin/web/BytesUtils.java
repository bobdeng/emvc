package com.handwin.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BytesUtils {
	public static void writeIntDebug(OutputStream stream, int nData)
			throws IOException {
		// stream.write((String.valueOf(nData)+":").getBytes());
		for (int i = 0; i < 4; i++) {
			String tmp = Integer
					.toHexString((nData & (0x000000ff << (i * 8))) >> i * 8);
			if (tmp.length() == 1)
				tmp = "0" + tmp;
			stream.write(tmp.getBytes());
			stream.write(" ".getBytes());

		}

	}

	public static void writeShortDebug(OutputStream stream, int nData)
			throws IOException {
		// stream.write((String.valueOf(nData)+":").getBytes());
		for (int i = 0; i < 2; i++) {
			String tmp = Integer
					.toHexString((nData & (0x000000ff << (i * 8))) >> i * 8);
			if (tmp.length() == 1)
				tmp = "0" + tmp;
			stream.write(tmp.getBytes());
			stream.write(" ".getBytes());

		}
	}

	public static void writeByteDebug(OutputStream stream, int nData)
			throws IOException {

		String tmp = Integer.toHexString((nData & (0x000000ff)));
		if (tmp.length() == 1)
			tmp = "0" + tmp;
		stream.write(tmp.getBytes());
		stream.write(" ".getBytes());
		// stream.write(Integer.toString(nData).getBytes());

	}

	public static void writeStringDebug(OutputStream stream, String strMsg)
			throws IOException {
		// stream.write((strMsg).getBytes());
		short sLen = (strMsg == null) ? 0 : (short) strMsg.length();
		writeShortDebug(stream, sLen);
		if (strMsg != null) {
			byte[] bTmp = strMsg.getBytes();
			for (int i = 0; i < bTmp.length; i++) {
				writeByteDebug(stream, bTmp[i]);

			}
		}

	}

	public static void writeStringDebug(OutputStream stream, String strMsg,
			int length) throws IOException {
		// stream.write((strMsg).getBytes());
		short sLen = (strMsg == null) ? 0 : (short) strMsg.length();
		writeShortDebug(stream, sLen);
		if (strMsg != null) {
			byte[] bTmp = strMsg.getBytes();
			for (int i = 0; i < bTmp.length; i++) {
				writeByteDebug(stream, bTmp[i]);

			}
			for (int i = bTmp.length; i < length; i++) {
				writeByteDebug(stream, 0);
			}
		}

	}

	public static void writeUTFDebug(OutputStream stream, String strMsg)
			throws IOException {
		stream.write((strMsg).getBytes());
		/*
		 * short sLen = (strMsg == null) ? 0 : (short) strMsg.length();
		 * writeShortDebug(stream, sLen); if (strMsg != null) { for (int i = 0;
		 * i < sLen; i++) { int nData = strMsg.charAt(i);
		 * writeShortDebug(stream, nData);
		 *  } }
		 */

	}

	public static void writeByteArrayDebug(OutputStream stream, byte[] szData)
			throws IOException {
		short sLen = (szData == null) ? 0 : (short) szData.length;
		writeShortDebug(stream, sLen);
		if (sLen > 0) {
			for (int i = 0; i < sLen; i++)
				writeByteDebug(stream, new Integer(szData[i]));
		}
	}

	public static void writeInt(OutputStream stream, int nData)
			throws IOException {
		stream.write(nData);
		stream.write(nData >> 8);
		stream.write(nData >> 16);
		stream.write(nData >> 24);
	}

	public static void writeLong(OutputStream stream, long nData)
			throws IOException {
		byte[] writeBuffer = new byte[8];
		writeBuffer[0] = (byte) (int) (nData >>> 0);
		writeBuffer[1] = (byte) (int) (nData >>> 8);
		writeBuffer[2] = (byte) (int) (nData >>> 16);
		writeBuffer[3] = (byte) (int) (nData >>> 24);
		writeBuffer[4] = (byte) (int) (nData >>> 32);
		writeBuffer[5] = (byte) (int) (nData >>> 40);
		writeBuffer[6] = (byte) (int) (nData >>> 48);
		writeBuffer[7] = (byte) (int) (nData >>> 56);
		stream.write(writeBuffer, 0, 8);
	}

	public static void writeShort(OutputStream stream, int nData)
			throws IOException {
		stream.write(nData);
		stream.write(nData >> 8);
	}

	public static void writeByte(OutputStream stream, int nData)
			throws IOException {
		stream.write(new Integer(nData).byteValue());

	}

	public static void writeString(OutputStream stream, String strMsg)
			throws IOException {
		short sLen = (strMsg == null) ? 0 : (short) strMsg.length();
		writeShort(stream, sLen);
		if (sLen > 0)
			stream.write(strMsg.getBytes());
	}

	public static void writeUTFString(OutputStream stream, String strMsg)
			throws IOException {

		short sLen = (strMsg == null) ? 0
				: (short) strMsg.getBytes("utf-8").length;
		writeShort(stream, sLen);
		if (sLen > 0)
			stream.write(strMsg.getBytes("utf-8"));
	}

	public static void writeString(OutputStream stream, String strMsg,
			int length) throws IOException {
		short sLen = (strMsg == null) ? 0 : (short) strMsg.length();
		writeShort(stream, sLen);
		if (sLen > 0)
			stream.write(strMsg.getBytes());
		for (int i = sLen; i < length; i++) {
			stream.write(new Integer(0).byteValue());
		}
	}

	public static void writeCharArray(OutputStream stream, String strMsg,
			int length) throws IOException {
		byte[] rlt = strMsg.getBytes();
		/*
		 * int sLen = rlt.length; for (byte b : rlt) { writeByte(stream, b); }
		 */
		int len = strMsg.length();
		for (short i = 0; i < len; i++) {
			int nData = strMsg.charAt(i);
			stream.write(nData);
			stream.write(nData >> 8);
		}
		for (int i = len; i < length; i++) {
			writeByte(stream, new Integer(0).byteValue());
			writeByte(stream, new Integer(0).byteValue());
		}

	}

	public static void writeCharArrayDebug(OutputStream stream, String strMsg,
			int length) throws IOException {
		// System.out.println(length);
		int len = strMsg.length();
		for (short i = 0; i < len; i++) {
			int nData = strMsg.charAt(i);
			writeByteDebug(stream, nData);
			writeByteDebug(stream, nData >> 8);
		}
		// ����0
		for (int i = len; i < length; i++) {
			writeByteDebug(stream, new Integer(0).byteValue());
			writeByteDebug(stream, new Integer(0).byteValue());
		}

	}

	public static void writeUTF(OutputStream stream, String strMsg)
			throws IOException {
		int sLen = (strMsg == null) ? 0 : strMsg.length();
		writeShort(stream, sLen);

		for (short i = 0; i < sLen; i++) {
			int nData = strMsg.charAt(i);
			stream.write(nData);
			stream.write(nData >> 8);
		}
	}

	public static void writeUTF8(OutputStream stream, String strMsg)
			throws IOException {
		int sLen = (strMsg == null) ? 0 : strMsg.getBytes("utf-8").length;
		writeShort(stream, sLen);

		stream.write(strMsg.getBytes("utf-8"));
	}

	public static void writeByteArray(OutputStream stream, byte[] szData)
			throws IOException {
		int sLen = (szData == null) ? 0 : szData.length;
		writeShort(stream, sLen);
		if (sLen > 0) {
			stream.write(szData);

		}

	}

	public static void writeByteArrayNoLen(OutputStream stream, byte[] szData)
			throws IOException {
		short sLen = (szData == null) ? 0 : (short) szData.length;
		if (sLen > 0) {
			stream.write(szData);

		}
	}

	public static void writeByteArrayNoLenDebug(OutputStream stream,
			byte[] szData) throws IOException {
		short sLen = (szData == null) ? 0 : (short) szData.length;
		for (byte b : szData) {
			writeByteDebug(stream, b);
		}
	}

	static public int bytesToInt(byte[] szSrc, int nStart, int nLength) {
		if (nLength > 4) {
			return -1;
		}
		int nResult = 0;
		int nEnd = nStart + nLength - 1;
		for (; nEnd >= nStart; nEnd--) {
			nResult <<= 8;
			if (szSrc[nEnd] < 0)
				nResult += 0x100;
			nResult += szSrc[nEnd];
		}
		return nResult;
	}

	static public String bytesToUtf(byte[] szBuf, int nIndex, int nLen) {
		if (nLen <= 0)
			return null;
		char[] m_strSteamTemp = new char[nLen];
		for (int i = 0; i < nLen; i++, nIndex += 2) {
			int nData = bytesToInt(szBuf, nIndex, 2);
			// if (nData < 0) nData -= 0x80;
			m_strSteamTemp[i] = (char) nData;
		}
		return new String(m_strSteamTemp, 0, nLen);
	}

	static public String bytesToString(byte[] szBuf, int nIndex, int nLen) {
		if (nLen <= 0)
			return null;
		char[] m_strSteamTemp = new char[nLen];
		for (int i = 0; i < nLen; i++, nIndex++) {
			int nData = bytesToInt(szBuf, nIndex, 1);
			// if (nData < 0) nData -= 0x80;
			m_strSteamTemp[i] = (char) nData;
		}
		return new String(m_strSteamTemp, 0, nLen);
	}

	static public int ConvertArrayToInt(byte[] szSrc, int nStart, int nLength) {
		if (nLength > 4) {
			return -1;
		}
		int nResult = 0;
		if (szSrc.length < nStart + nLength)
			return 0;
		int nEnd = nStart + nLength - 1;
		for (; nEnd >= nStart; nEnd--) {
			nResult <<= 8;
			if (szSrc[nEnd] < 0)
				nResult += 0x100;
			nResult += szSrc[nEnd];
		}

		return nResult;
	}

	public static void main(String[] args) throws IOException {
		ByteArrayOutputStream temp = new ByteArrayOutputStream();
		writeLong(temp, 12344555222L);
		ByteArrayInputStream tempi = new ByteArrayInputStream(temp
				.toByteArray());
		System.out.println(readLong(tempi, 8));
	}

	public static byte[] readByteArray(InputStream input, int len)
			throws IOException {
		if (len > 1024 * 1024)
			return null;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int total = 0;
		while (total < len) {
			int r = 0;
			if ((len - total) >= 1024)
				r = input.read(buffer);
			else
				r = input.read(buffer, 0, len - total);
			out.write(buffer, 0, r);
			out.flush();
			total += r;
		}
		return out.toByteArray();

	}

	public static byte[] m_szSteamTemp = new byte[4];
	public static char[] m_strSteamTemp = new char[16];

	public static int ReadInt(InputStream stream, int nLen) throws IOException {
		stream.read(m_szSteamTemp, 0, nLen);
		return ConvertArrayToInt(m_szSteamTemp, 0, nLen);
	}

	public static int ReadIntOther(InputStream stream, int nLen)
			throws IOException {
		stream.read(m_szSteamTemp, 0, nLen);
		return ConvertArrayToIntOther(m_szSteamTemp, 0, nLen);
	}

	public static long readLong(InputStream stream, int nLen) {
		byte[] data = new byte[nLen];
		try {
			stream.read(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long rlt = 0;
		for (int i = data.length - 1; i >= 0; i--) {
			rlt <<= 8;
			rlt += (long) data[i] & 0xFF;
		}
		return rlt;
	}

	public static int ConvertArrayToIntOther(byte[] szSrc, int nStart,
			int nLength) {
		if (nLength > 4) {
			return -1;
		}
		int nResult = 0;
		if (szSrc.length < nStart + nLength)
			return 0;
		int nEnd = nStart;
		for (; nEnd <= nStart + nLength - 1; nEnd++) {
			nResult <<= 8;
			if (szSrc[nEnd] < 0)
				nResult += 0x100;
			nResult += szSrc[nEnd];
		}

		return nResult;
	}

	public static String ReadUTF(byte[] szBuf, int nIndex) {
		if (szBuf == null)
			return null;
		try {
			int nLen = ConvertArrayToInt(szBuf, nIndex, 2);
			nIndex += 2;
			return ReadUTF(szBuf, nIndex, nLen);
		} catch (Exception e) {
			return null;
		}
	}

	public static String ReadUTF(byte[] szBuf, int nIndex, int nLen)
			throws Exception {

		if (nLen <= 0)
			return null;
		StringBuffer sbResult = new StringBuffer();
		// if (m_strSteamTemp.length < nLen)
		// m_strSteamTemp = new char[nLen];
		for (int i = 0; i < nLen; i++, nIndex += 2) {
			int nData = ConvertArrayToInt(szBuf, nIndex, 2);
			// if (nData < 0) nData -= 0x80;
			sbResult.append((char) nData);
			// m_strSteamTemp[i] = (char)nData;
		}

		return sbResult.toString();

	}
	
	public static String ReadUTF(InputStream stream) {
		// char����
		try {
			int nLen = ReadInt(stream, 2);

			return ReadUTF(stream, nLen);
		} catch (Exception e) {
			return null;
		}
	}

	public static String ReadUTF(InputStream stream, int nLen) throws Exception {
		if (nLen <= 0)
			return null;
		// System.out.println("len===="+nLen);
		if (m_strSteamTemp.length < nLen)
			m_strSteamTemp = new char[nLen];
		for (int i = 0; i < nLen; i++) {
			int nData = ReadInt(stream, 2);
			// System.out.println("Data==="+nData);
			m_strSteamTemp[i] = (char) nData;
		}
		return new String(m_strSteamTemp, 0, nLen);

	}

}
