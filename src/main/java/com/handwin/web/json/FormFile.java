package com.handwin.web.json;

import java.io.File;

import org.apache.commons.fileupload.FileItem;

public class FormFile {

	private String fileName;
	private FileItem file;
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public FileItem getFile() {
		return file;
	}
	public void setFile(FileItem file) {
		
		this.file = file;
	}
	public static void main(String[] args)
	{
		System.out.println(FormFile[].class.getName().hashCode());
	}
	public FormFile(FileItem file) {
		super();
		this.file = file;
	}

}
