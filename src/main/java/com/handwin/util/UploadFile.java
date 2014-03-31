package com.handwin.util;

public class UploadFile {
	private String filedName;
	private String fileName;
	private byte[] data;
	private String mimeType;
	public String getFiledName() {
		return filedName;
	}
	public void setFiledName(String filedName) {
		this.filedName = filedName;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public byte[] getData() {
		return data;
	}
	
	public String getMimeType() {
		return mimeType;
	}
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}
	/**
	 * @param filedName 涓婁紶鏃跺弬鏁板悕
	 * @param fileName 鏂囦欢鍚�
	 * @param data 鏂囦欢鍐呭
	 */
	public UploadFile(String filedName,String fileName,String mimeType, byte[] data) {
		super();
		this.filedName=filedName;
		this.fileName = fileName;
		this.data = data;
		this.mimeType=mimeType;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
}
