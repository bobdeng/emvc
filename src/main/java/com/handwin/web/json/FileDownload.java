package com.handwin.web.json;

public interface FileDownload {

	public String getFileName();
	public String getContentType();
	public byte[] getFileData();
	
}
